package com.cfs.RideTap.booking.dto.response;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private String originStation;
    private String destinationStation;
    private double fare;
    private BookingStatus bookingStatus;
    private String qrCodePath;
    private LocalDateTime dateTime;
    private LocalDateTime expiresAt;
    private boolean expired;
    private PaymentMethod paymentMethod;
    private String razorpayPaymentId;
    private String metroCardNumber;
    private String passengerName;
}
