package com.cfs.RideTap.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "revoked_tokens",
        indexes = {
                @Index(name = "idx_revoked_token_hash", columnList = "token_hash")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public RevokedToken(String tokenHash, LocalDateTime expiryDate) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
    }
}
