package com.cfs.RideTap.payment.api;

import com.cfs.RideTap.payment.dto.request.CreatePaymentOrderRequest;
import com.cfs.RideTap.payment.dto.request.VerifyPaymentRequest;
import com.cfs.RideTap.payment.dto.response.CreatePaymentResponse;
import com.cfs.RideTap.payment.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Payment",
        description = "Razorpay Payment Endpoints for Ride Booking & Metro Card Recharge"
)
public interface PaymentApi {

    @Operation(
            summary = "Create Razorpay Order",
            description = "Create a Razorpay payment order for ticket booking or Metro Card recharge"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Razorpay order created"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<CreatePaymentResponse> createPaymentOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request,
            Authentication authentication);

    @Operation(
            summary = "Verify Payment",
            description = "Verify Razorpay payment signature and confirm booking or credit Metro Card"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment or signature")
    })
    ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            Authentication authentication);

    @Operation(
            summary = "Get Payment by Booking",
            description = "Get payment details for a specific booking"
    )
    ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable Long bookingId,
            Authentication authentication);

    @Operation(
            summary = "Get My Payment History",
            description = "Get the authenticated user's payment transaction history, newest first"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<Page<PaymentResponse>> getMyPayments(
            Pageable pageable,
            Authentication authentication);
}
