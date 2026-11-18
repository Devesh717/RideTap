package com.cfs.RideTap.payment.service.interfaces;

import com.cfs.RideTap.payment.dto.request.CreatePaymentOrderRequest;
import com.cfs.RideTap.payment.dto.request.VerifyPaymentRequest;
import com.cfs.RideTap.payment.dto.response.CreatePaymentResponse;
import com.cfs.RideTap.payment.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    CreatePaymentResponse createPaymentOrder(
            String userEmail,
            CreatePaymentOrderRequest request);

    PaymentResponse verifyPayment(
            String userEmail,
            VerifyPaymentRequest request);

    PaymentResponse getPaymentByBooking(
            String userEmail,
            Long bookingId);

    Page<PaymentResponse> getMyPayments(
            String userEmail,
            Pageable pageable);
}
