package com.cfs.RideTap.payment.entity;

import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.user.model.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_razorpay_order",
                        columnNames = "razorpay_order_id"
                ),
                @UniqueConstraint(
                        name = "uk_payment_razorpay_payment",
                        columnNames = "razorpay_payment_id"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String referenceNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(unique = true)
    private String razorpayPaymentId;

    @Column(length = 500)
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.CREATED;

    // Populated when purpose == TICKET_BOOKING
    private String originStation;
    private String destinationStation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Payment() {
    }

    public Long getId() { return id; }
    public String getReferenceNumber() { return referenceNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public PaymentPurpose getPurpose() { return purpose; }
    public PaymentStatus getStatus() { return status; }
    public String getOriginStation() { return originStation; }
    public String getDestinationStation() { return destinationStation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public UserEntity getUser() { return user; }
    public Booking getBooking() { return booking; }

    public void setId(Long id) { this.id = id; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
    public void setPurpose(PaymentPurpose purpose) { this.purpose = purpose; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setOriginStation(String originStation) { this.originStation = originStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public void setUser(UserEntity user) { this.user = user; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
