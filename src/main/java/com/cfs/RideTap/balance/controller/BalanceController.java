package com.cfs.RideTap.balance.controller;

import com.cfs.RideTap.balance.api.BalanceApi;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/balance")
@CrossOrigin(origins = "http://localhost:3000")
public class BalanceController implements BalanceApi {

    @Autowired
    private UserRepository userRepository;

    private UserEntity resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    @GetMapping
    public ResponseEntity<Double> getBalance(Authentication authentication) {
        UserEntity user = resolveAuthenticatedUser(authentication);
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }
}
