package com.cfs.RideTap.booking.dto.response;

import java.time.LocalDateTime;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.user.dto.response.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Station originStation;
    private Station destinationStation;
    private LocalDateTime dateTime;
    private LocalDateTime expiresAt;
    private boolean expired;
    private BookingStatus bookingStatus;
    private PaymentMethod paymentMethod;
    private String razorpayPaymentId;
    private UserBasicDto user;
    private double fare;
    private String qrPath;
}
