package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.dtos.userDTO.UserDTO;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.UserServices;
import com.cfs.RideTap.utils.Mappers.UserDTOMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserTool {

    @Autowired
    private UserServices userServices;

    @Tool(
            description = """
    Show the profile of the currently logged-in user.
    Never ask for email.
    """
    )
    public UserDTO getUserInfo() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        UserEntity user =
                userServices.findByEmail(email);

        System.out.println(
                "USER FOUND = "
                        + user.getEmail());

        return UserDTOMapper.map(user);
    }
}
