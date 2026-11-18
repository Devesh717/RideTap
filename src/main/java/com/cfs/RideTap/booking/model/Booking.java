package com.cfs.RideTap.booking.model;

import java.time.LocalDateTime;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.payment.entity.PaymentMethod;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.user.model.UserEntity;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    public static final int TICKET_VALIDITY_HOURS = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonIgnoreProperties("bookings")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "origin_station_id")
    private Station originStation;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "destination_station_id")
    private Station destinationStation;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateTime;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Min(0)
    private double fare;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BookingStatus bookingStatus;

    @Column(name = "qr_code_path", columnDefinition = "TEXT")
    private String qrCodePath;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.METROCARD;

    private String razorpayPaymentId;

    @PrePersist
    public void prePersist() {
        if (this.dateTime == null) {
            this.dateTime = LocalDateTime.now();
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.dateTime.plusHours(TICKET_VALIDITY_HOURS);
        }
    }

    public LocalDateTime getEffectiveExpiresAt() {
        if (this.expiresAt != null) {
            return this.expiresAt;
        }
        return this.dateTime != null ? this.dateTime.plusHours(TICKET_VALIDITY_HOURS) : null;
    }

    public boolean isExpired() {
        if (this.bookingStatus == BookingStatus.EXPIRED) {
            return true;
        }
        LocalDateTime expiry = getEffectiveExpiresAt();
        return expiry != null && LocalDateTime.now().isAfter(expiry);
    }
}