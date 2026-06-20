package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.UserServices;
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
    Show balance of the currently logged-in user.
    Never ask for metro card number.
    """
    )
    public String getBalance() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        UserEntity user =
                userServices.findByEmail(
                        email);

        return "Current balance: ₹" + user.getBalance();
    }


}
