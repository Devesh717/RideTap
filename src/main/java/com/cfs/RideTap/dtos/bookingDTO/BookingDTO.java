package com.cfs.RideTap.dtos.bookingDTO;

import java.time.LocalDateTime;

import com.cfs.RideTap.dtos.userDTO.UserBasicDto;
import com.cfs.RideTap.enums.BookingStatus;
import com.cfs.RideTap.models.Station;
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
  private BookingStatus bookingStatus;
  private UserBasicDto user;
  private double fare;
  private String qrPath;
}
