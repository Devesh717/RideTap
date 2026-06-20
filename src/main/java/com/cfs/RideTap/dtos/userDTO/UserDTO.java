package com.cfs.RideTap.dtos.userDTO;

import java.time.LocalDateTime;
import java.util.List;

import com.cfs.RideTap.dtos.bookingDTO.BookingBasicDTO;

import com.cfs.RideTap.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private String metroCardNumber;
  private String holderName;
  private String email;
  private String mobileNumber;
  private double balance;
  private LocalDateTime createdAt;
  private List<Role> roles;
  private List<BookingBasicDTO> bookings;
}
