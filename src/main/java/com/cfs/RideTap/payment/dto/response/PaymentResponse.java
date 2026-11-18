package com.cfs.RideTap.payment.dto.response;

import com.cfs.RideTap.booking.dto.response.BookingResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long paymentId;
    private String referenceNumber;
    private String purpose;
    private BigDecimal amount;
    private String currency;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private Double updatedBalance;
    private BookingResponse booking;

    public PaymentResponse(
            Long paymentId,
            String referenceNumber,
            String purpose,
            BigDecimal amount,
            String currency,
            String razorpayOrderId,
            String razorpayPaymentId,
            String status,
            LocalDateTime createdAt,
            LocalDateTime paidAt,
            Double updatedBalance,
            BookingResponse booking) {
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.purpose = purpose;
        this.amount = amount;
        this.currency = currency;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.updatedBalance = updatedBalance;
        this.booking = booking;
    }

    public Long getPaymentId() { return paymentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getPurpose() { return purpose; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public Double getUpdatedBalance() { return updatedBalance; }
    public BookingResponse getBooking() { return booking; }
}
