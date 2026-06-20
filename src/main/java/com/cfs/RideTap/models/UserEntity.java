package com.cfs.RideTap.models;

import com.cfs.RideTap.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String metroCardNumber;

  @NotBlank
  @Column(unique = true, nullable = false)
  private String email;

  @NotBlank
  @Column(unique = true, nullable = false)
  private String mobileNumber;

  @NotBlank
  @Column(nullable = false)
  private String password;

  @NotBlank
  @Column(nullable = false)
  private String holderName;

  @Min(0)
  private double balance;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(
          name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "role")
  private Set<Role> roles;

  private LocalDateTime createdAt;

  @JsonIgnoreProperties("user")
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Booking> bookings;

  public void deduct(double money){
    if(money <= balance){
      this.balance -= money;
    }
  }

  public void recharge(double money){
    this.balance += money;
  }

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
  }
}
