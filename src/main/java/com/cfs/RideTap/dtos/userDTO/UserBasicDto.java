package com.cfs.RideTap.dtos.userDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDto {
  private String holderName;
  private String email;
  private String mobileNumber;
  private String metroCardNumber;
}
