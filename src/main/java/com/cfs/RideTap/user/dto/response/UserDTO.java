package com.cfs.RideTap.user.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.cfs.RideTap.booking.dto.response.BookingBasicDTO;
import com.cfs.RideTap.user.enums.Role;
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
