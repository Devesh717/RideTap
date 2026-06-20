package com.cfs.RideTap.dtos.authDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String message;
  private String token;
}
