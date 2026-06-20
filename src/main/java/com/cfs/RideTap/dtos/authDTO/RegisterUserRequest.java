package com.cfs.RideTap.dtos.authDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
  @NotBlank(message = "Holder name is mandatory for registration")
  private String holderName;

  @NotBlank(message = "Email is required")
  @Pattern(
          regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
          message = "Invalid email format"
  )
  private String email;

  @NotBlank
  private String mobileNumber;

  @NotBlank(message = "Password name is mandatory for registration")
  @Pattern(
          regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
          message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit, and one special character"
  )
  private String password;

  private boolean admin;
}
