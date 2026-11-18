package com.cfs.RideTap.payment.dto.request;

import com.cfs.RideTap.payment.entity.PaymentPurpose;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreatePaymentOrderRequest {

    @NotNull(message = "Payment purpose is required")
    private PaymentPurpose purpose; // TICKET_BOOKING or METROCARD_RECHARGE

    // Required when purpose == METROCARD_RECHARGE
    private BigDecimal amount;

    // Required when purpose == TICKET_BOOKING
    private String originStation;
    private String destinationStation;

    public PaymentPurpose getPurpose() { return purpose; }
    public BigDecimal getAmount() { return amount; }
    public String getOriginStation() { return originStation; }
    public String getDestinationStation() { return destinationStation; }

    public void setPurpose(PaymentPurpose purpose) { this.purpose = purpose; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setOriginStation(String originStation) { this.originStation = originStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }
}
