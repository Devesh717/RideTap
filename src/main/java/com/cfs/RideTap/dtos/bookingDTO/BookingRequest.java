package com.cfs.RideTap.dtos.bookingDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
  @NotBlank(message = "Origin station name is required")
  private String originStation;
  
  @NotBlank(message = "Destination station name is required")
  private String destinationStation;
}
