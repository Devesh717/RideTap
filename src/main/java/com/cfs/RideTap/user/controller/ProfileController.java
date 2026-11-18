package com.cfs.RideTap.user.controller;

import com.cfs.RideTap.user.api.ProfileApi;
import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.mapper.UserDTOMapper;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/profile")
public class ProfileController implements ProfileApi {
  @Autowired
  private UserServices userServices;

  @Override
  @GetMapping
  public ResponseEntity<UserDTO> getProfile(HttpServletRequest request) {
    String email = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
      email = authentication.getName();
    }

    if (email == null) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        email = (String) session.getAttribute("email");
      }
    }

    if (email == null || email.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    UserEntity userEntity = userServices.findByEmail(email);
    UserDTO userDTO = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }
}
