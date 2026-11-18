package com.cfs.RideTap.auth.service.implementations;

import java.util.Collection;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.auth.service.interfaces.AuthServices;
import com.cfs.RideTap.common.security.UserDetailsServiceImpl;
import com.cfs.RideTap.common.util.JwtUtil;
import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServicesImpl implements AuthServices {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserServices userServices;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  public UserDTO register(RegisterUserRequest registerRequest) {
    UserEntity savedUser = userServices.register(registerRequest);
    return modelMapper.map(savedUser, UserDTO.class);
  }

  @Override
  public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
    String email = loginRequest.getEmail().trim().toLowerCase();

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserEntity user = userServices.findByEmail(email);
    String primaryRole = (user.getRoles() != null && !user.getRoles().isEmpty())
            ? user.getRoles().get(0).name()
            : Role.USER.name();

    String message = "User login successfully";
    String token = jwtUtil.generateToken(authentication);

    storeUserInSession(request, loginRequest);

    return new LoginResponse(token, message, primaryRole, user.getHolderName());
  }

  private void storeUserInSession(HttpServletRequest request, LoginRequest loginRequest) {
    UserEntity user = userServices.findByEmail(loginRequest.getEmail());
    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.getEmail());

    HttpSession session = request.getSession();
    session.setAttribute("email", user.getEmail());
    session.setAttribute("metroCardNumber", user.getMetroCardNumber());
    session.setAttribute("isAdmin", includesAdminAuthority(userDetails.getAuthorities()));
  }

  private boolean includesAdminAuthority(Collection<? extends GrantedAuthority> collection) {
    for (GrantedAuthority grantedAuthority : collection) {
      if (Role.ADMIN.name().equals(grantedAuthority.getAuthority())) {
        return true;
      }
    }
    return false;
  }
}
