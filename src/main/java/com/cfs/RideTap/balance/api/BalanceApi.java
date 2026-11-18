package com.cfs.RideTap.balance.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Balance", description = "Metro Card Balance Endpoints")
public interface BalanceApi {

    @Operation(summary = "Get Current Metro Card Balance")
    ResponseEntity<Double> getBalance(Authentication authentication);
}
