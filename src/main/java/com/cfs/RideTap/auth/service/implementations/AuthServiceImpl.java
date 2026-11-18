package com.cfs.RideTap.auth.service.implementations;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.auth.dto.response.RegisterResponse;
import com.cfs.RideTap.auth.entity.RevokedToken;
import com.cfs.RideTap.auth.repository.RevokedTokenRepository;
import com.cfs.RideTap.auth.service.interfaces.AuthService;
import com.cfs.RideTap.common.util.JwtUtil;
import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserServices userServices;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RevokedTokenRepository revokedTokenRepository;

    @Value("${app.admin.email:admin@ridetap.com}")
    private String adminEmail;

    @Value("${app.admin.phone:9999999999}")
    private String adminPhoneNumber;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserServices userServices,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            RevokedTokenRepository revokedTokenRepository) {
        this.userRepository = userRepository;
        this.userServices = userServices;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    public RegisterResponse register(RegisterUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (email.equalsIgnoreCase(adminEmail.trim())) {
            throw new IllegalArgumentException("This email is reserved for the administrator");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String mobileNumber = request.getMobileNumber().trim();

        if (mobileNumber.equals(adminPhoneNumber.trim())) {
            throw new IllegalArgumentException("This mobile number is reserved for the administrator");
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Mobile number is already registered");
        }

        request.setEmail(email);
        request.setMobileNumber(mobileNumber);
        request.setHolderName(request.getHolderName().trim());

        UserEntity savedUser = userServices.register(request);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new RegisterResponse(
                "Registration successful",
                savedUser.getId(),
                token,
                savedUser.getMetroCardNumber()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email is not registered"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            String token = jwtUtil.generateToken(authentication.getName());
            String primaryRole = (user.getRoles() != null && !user.getRoles().isEmpty())
                    ? user.getRoles().get(0).name()
                    : Role.USER.name();

            return new LoginResponse(
                    token,
                    "Login successful",
                    primaryRole,
                    user.getHolderName()
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Wrong password");
        }
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Bearer token is required");
        }

        String tokenHash = hashToken(token);
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        if (!revokedTokenRepository.existsByTokenHashAndExpiryDateAfter(tokenHash, LocalDateTime.now())) {
            revokedTokenRepository.save(new RevokedToken(tokenHash, expiryDate));
        }
    }

    public String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
