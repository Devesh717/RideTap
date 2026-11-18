package com.cfs.RideTap.booking.dto.request;

import com.cfs.RideTap.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotBlank(message = "Origin station is required")
    private String originStation;

    @NotBlank(message = "Destination station is required")
    private String destinationStation;

    // METROCARD (default) or RAZORPAY
    private PaymentMethod paymentMethod = PaymentMethod.METROCARD;

    private String razorpayPaymentId;
}
