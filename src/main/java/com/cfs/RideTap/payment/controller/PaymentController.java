package com.cfs.RideTap.payment.controller;

import com.cfs.RideTap.payment.api.PaymentApi;
import com.cfs.RideTap.payment.dto.request.CreatePaymentOrderRequest;
import com.cfs.RideTap.payment.dto.request.VerifyPaymentRequest;
import com.cfs.RideTap.payment.dto.response.CreatePaymentResponse;
import com.cfs.RideTap.payment.dto.response.PaymentResponse;
import com.cfs.RideTap.payment.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentResponse> createPaymentOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                paymentService.createPaymentOrder(
                        authentication.getName(),
                        request)
        );
    }

    @Override
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                paymentService.verifyPayment(
                        authentication.getName(),
                        request)
        );
    }

    @Override
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBooking(
                        authentication.getName(),
                        bookingId)
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getMyPayments(
            Pageable pageable,
            Authentication authentication) {

        return ResponseEntity.ok(
                paymentService.getMyPayments(
                        authentication.getName(),
                        pageable)
        );
    }
}
