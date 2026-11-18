package com.cfs.RideTap.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private Long userId;
    private String accessToken;
    private String metroCardNumber;
}
