package com.cfs.RideTap.auth.config;

import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AdminAccountSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name:RideTap Admin}")
    private String adminName;

    @Value("${app.admin.email:admin@ridetap.com}")
    private String adminEmail;

    @Value("${app.admin.phone:9999999999}")
    private String adminPhone;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    public AdminAccountSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String normalizedEmail = adminEmail.trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail).ifPresentOrElse(
                existing -> {
                    if (existing.getRoles() == null || !existing.getRoles().contains(Role.ADMIN)) {
                        existing.setRoles(new ArrayList<>(List.of(Role.ADMIN)));
                        userRepository.save(existing);
                    }
                },
                () -> {
                    UserEntity admin = new UserEntity();
                    admin.setHolderName(adminName.trim());
                    admin.setEmail(normalizedEmail);
                    admin.setMobileNumber(adminPhone.trim());
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    admin.setRoles(new ArrayList<>(List.of(Role.ADMIN)));
                    admin.setMetroCardNumber("MC-ADMIN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                    admin.setBalance(0.0);
                    admin.setBookings(new ArrayList<>());
                    userRepository.save(admin);
                }
        );
    }
}
