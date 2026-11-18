package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.mapper.UserDTOMapper;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserTool {

    @Autowired
    private UserServices userServices;

    @Tool(
            description = """
    Show the profile details (holder name, email, mobile number, Metro Card number, and balance)
    of the currently logged-in user. Never ask for email.
    """
    )
    public UserDTO getUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userServices.findByEmail(email);
        return UserDTOMapper.map(user);
    }
}
