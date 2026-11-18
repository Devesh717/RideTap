package com.cfs.RideTap.payment.service.implementations;

import com.cfs.RideTap.booking.dto.request.BookingRequest;
import com.cfs.RideTap.booking.dto.response.BookingResponse;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingServices;
import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import com.cfs.RideTap.payment.dto.request.CreatePaymentOrderRequest;
import com.cfs.RideTap.payment.dto.request.VerifyPaymentRequest;
import com.cfs.RideTap.payment.dto.response.CreatePaymentResponse;
import com.cfs.RideTap.payment.dto.response.PaymentResponse;
import com.cfs.RideTap.payment.entity.Payment;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import com.cfs.RideTap.payment.entity.PaymentPurpose;
import com.cfs.RideTap.payment.entity.PaymentStatus;
import com.cfs.RideTap.payment.repository.PaymentRepository;
import com.cfs.RideTap.payment.service.interfaces.PaymentService;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BookingServices bookingServices;
    private final FareCalculator fareCalculator;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            BookingServices bookingServices,
            FareCalculator fareCalculator,
            RazorpayClient razorpayClient) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.bookingServices = bookingServices;
        this.fareCalculator = fareCalculator;
        this.razorpayClient = razorpayClient;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPaymentOrder(
            String userEmail,
            CreatePaymentOrderRequest request) {

        UserEntity user = getAuthenticatedUser(userEmail);

        BigDecimal amount;
        if (request.getPurpose() == PaymentPurpose.TICKET_BOOKING) {
            if (request.getOriginStation() == null || request.getOriginStation().isBlank()
                    || request.getDestinationStation() == null || request.getDestinationStation().isBlank()) {
                throw new IllegalArgumentException(
                        "Origin and destination stations are required for ticket booking");
            }
            if (request.getOriginStation().equalsIgnoreCase(request.getDestinationStation())) {
                throw new IllegalArgumentException(
                        "Origin and destination stations must be different");
            }
            double fare = fareCalculator.calculate(
                    request.getOriginStation(),
                    request.getDestinationStation());
            amount = BigDecimal.valueOf(fare).setScale(2, RoundingMode.HALF_UP);
        } else {
            if (request.getAmount() == null
                    || request.getAmount().compareTo(BigDecimal.TEN) < 0) {
                throw new IllegalArgumentException(
                        "Minimum Metro Card recharge amount is ₹10");
            }
            amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        }

        long amountInPaise = amount
                .movePointRight(2)
                .longValueExact();

        String referenceNumber = "RT-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();

        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", currency);
            options.put("receipt", referenceNumber);

            JSONObject notes = new JSONObject();
            notes.put("purpose", request.getPurpose().name());
            notes.put("metroCardNumber", user.getMetroCardNumber());
            notes.put("email", user.getEmail());
            options.put("notes", notes);

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(options);

            Payment payment = new Payment();
            payment.setReferenceNumber(referenceNumber);
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setPurpose(request.getPurpose());
            payment.setStatus(PaymentStatus.CREATED);
            payment.setUser(user);

            if (request.getPurpose() == PaymentPurpose.TICKET_BOOKING) {
                payment.setOriginStation(request.getOriginStation());
                payment.setDestinationStation(request.getDestinationStation());
            }

            Payment savedPayment = paymentRepository.save(payment);

            return new CreatePaymentResponse(
                    savedPayment.getId(),
                    savedPayment.getReferenceNumber(),
                    savedPayment.getRazorpayOrderId(),
                    razorpayKeyId,
                    amount,
                    currency,
                    savedPayment.getPurpose().name(),
                    user.getHolderName(),
                    user.getEmail(),
                    user.getMobileNumber(),
                    user.getMetroCardNumber()
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create Razorpay payment order",
                    exception);
        }
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(
            String userEmail,
            VerifyPaymentRequest request) {

        UserEntity user = getAuthenticatedUser(userEmail);

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment order not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not authorized to verify this payment");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            BookingResponse existingBooking = payment.getBooking() != null
                    ? mapToBookingResponse(payment.getBooking())
                    : null;
            return toResponse(payment, user.getBalance(), existingBooking);
        }

        if (paymentRepository.existsByRazorpayPaymentId(
                request.getRazorpayPaymentId())) {
            throw new IllegalArgumentException(
                    "Payment has already been processed");
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", payment.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(
                    options,
                    razorpayKeySecret);

            if (!valid) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new IllegalArgumentException(
                        "Payment signature verification failed");
            }

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            BookingResponse bookingResponse = null;

            if (payment.getPurpose() == PaymentPurpose.METROCARD_RECHARGE) {
                user.recharge(payment.getAmount().doubleValue());
                userRepository.save(user);
            } else if (payment.getPurpose() == PaymentPurpose.TICKET_BOOKING) {
                BookingRequest bookingRequest = new BookingRequest();
                bookingRequest.setOriginStation(payment.getOriginStation());
                bookingRequest.setDestinationStation(payment.getDestinationStation());
                bookingRequest.setPaymentMethod(PaymentMethod.RAZORPAY);
                bookingRequest.setRazorpayPaymentId(request.getRazorpayPaymentId());

                Booking savedBooking = bookingServices.book(
                        user.getMetroCardNumber(),
                        bookingRequest);

                payment.setBooking(savedBooking);
                bookingResponse = mapToBookingResponse(savedBooking);
            }

            Payment savedPayment = paymentRepository.save(payment);

            return toResponse(savedPayment, user.getBalance(), bookingResponse);

        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Payment verification failed",
                    exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBooking(
            String userEmail,
            Long bookingId) {

        UserEntity user = getAuthenticatedUser(userEmail);

        Payment payment = paymentRepository
                .findByBookingId(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not authorized to access this payment");
        }

        BookingResponse bookingResponse = payment.getBooking() != null
                ? mapToBookingResponse(payment.getBooking())
                : null;

        return toResponse(payment, user.getBalance(), bookingResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getMyPayments(
            String userEmail,
            Pageable pageable) {

        UserEntity user = getAuthenticatedUser(userEmail);

        return paymentRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(payment -> toResponse(
                        payment,
                        user.getBalance(),
                        payment.getBooking() != null ? mapToBookingResponse(payment.getBooking()) : null));
    }

    private UserEntity getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    private PaymentResponse toResponse(
            Payment payment,
            Double updatedBalance,
            BookingResponse bookingResponse) {

        return new PaymentResponse(
                payment.getId(),
                payment.getReferenceNumber(),
                payment.getPurpose().name(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getPaidAt(),
                updatedBalance,
                bookingResponse
        );
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        boolean expired = booking.isExpired();
        BookingStatus effectiveStatus = (expired && booking.getBookingStatus() == BookingStatus.CONFIRMED)
                ? BookingStatus.EXPIRED
                : booking.getBookingStatus();

        return BookingResponse.builder()
                .id(booking.getId())
                .originStation(booking.getOriginStation() != null ? booking.getOriginStation().getName() : null)
                .destinationStation(booking.getDestinationStation() != null ? booking.getDestinationStation().getName() : null)
                .fare(booking.getFare())
                .bookingStatus(effectiveStatus)
                .qrCodePath(booking.getQrCodePath())
                .dateTime(booking.getDateTime())
                .expiresAt(booking.getExpiresAt())
                .expired(expired)
                .paymentMethod(booking.getPaymentMethod())
                .razorpayPaymentId(booking.getRazorpayPaymentId())
                .metroCardNumber(booking.getUser() != null ? booking.getUser().getMetroCardNumber() : null)
                .passengerName(booking.getUser() != null ? booking.getUser().getHolderName() : null)
                .build();
    }
}
