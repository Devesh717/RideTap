package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BalanceTool {

    @Autowired
    private UserServices userServices;

    @Tool(
            description = """
    Show the current RideTap Metro Card balance and card number of the currently logged-in user.
    Never ask for metro card number or email.
    """
    )
    public String getBalance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userServices.findByEmail(email);
        double balance = user.getBalance();
        String alert = balance < 50.0
                ? " (Low balance — consider topping up via Wallet -> Recharge Metro Card)"
                : "";

        return String.format("Metro Card: %s | Available Balance: ₹%.2f%s",
                user.getMetroCardNumber(), balance, alert);
    }
}
