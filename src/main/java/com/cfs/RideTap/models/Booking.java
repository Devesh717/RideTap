package com.cfs.RideTap.models;

import java.time.LocalDateTime;

import com.cfs.RideTap.enums.BookingStatus;
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

  @Min(0)
  private double fare;

  @Enumerated(EnumType.STRING)
  @NotNull
  private BookingStatus bookingStatus;

  @Column(name = "qr_code_path")
  private String qrCodePath;

  @PrePersist
  public void prePersist() {
    this.dateTime = LocalDateTime.now();
  }
}
