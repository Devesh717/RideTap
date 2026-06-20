package com.cfs.RideTap.dtos.eventDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreatedEvent {
    private Long bookingId;

    private String email;

    private String holderName;

    private String originStation;

    private String destinationStation;

    private double fare;

    private String qrCodePath;
}
