package com.cfs.RideTap.payment.dto.response;

import java.math.BigDecimal;

public class CreatePaymentResponse {

    private Long paymentId;
    private String referenceNumber;
    private String razorpayOrderId;
    private String keyId;
    private BigDecimal amount;
    private String currency;
    private String purpose;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String metroCardNumber;

    public CreatePaymentResponse(
            Long paymentId,
            String referenceNumber,
            String razorpayOrderId,
            String keyId,
            BigDecimal amount,
            String currency,
            String purpose,
            String customerName,
            String customerEmail,
            String customerPhone,
            String metroCardNumber) {
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.razorpayOrderId = razorpayOrderId;
        this.keyId = keyId;
        this.amount = amount;
        this.currency = currency;
        this.purpose = purpose;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.metroCardNumber = metroCardNumber;
    }

    public Long getPaymentId() { return paymentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getKeyId() { return keyId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getPurpose() { return purpose; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public String getMetroCardNumber() { return metroCardNumber; }
}
