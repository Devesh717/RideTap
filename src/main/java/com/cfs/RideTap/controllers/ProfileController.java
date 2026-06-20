package com.cfs.RideTap.controllers;

import com.cfs.RideTap.apis.ProfileApi;
import com.cfs.RideTap.dtos.userDTO.UserDTO;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.UserServices;
import com.cfs.RideTap.utils.Mappers.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/profile")
public class ProfileController implements ProfileApi {
  @Autowired
  private UserServices userServices;

  @GetMapping
  public ResponseEntity<UserDTO> getProfile(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");

    UserEntity userEntity = userServices.findByEmail(email);

    UserDTO userDTO = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }
}
