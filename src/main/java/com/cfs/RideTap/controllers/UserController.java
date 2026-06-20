package com.cfs.RideTap.controllers;

import com.cfs.RideTap.apis.UserApi;
import com.cfs.RideTap.dtos.userDTO.UserDTO;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.UserServices;
import com.cfs.RideTap.utils.Mappers.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;



import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController implements UserApi {
  @Autowired
  private UserServices userServices;

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAll() {
    List<UserEntity> userEntities = userServices.findAll();

    List<UserDTO> users = userEntities
        .stream()
        .map(user -> UserDTOMapper.map(user))
        .collect((Collectors.toList()));

    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("metro-card/{metroCardNumber}")
  public ResponseEntity<UserDTO> getByMetroCardNumber(@Valid @PathVariable String metroCardNumber) {
    UserEntity userEntity = userServices.findByMetroCardNumber(metroCardNumber);
    UserDTO user = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("email/{email}")
  public ResponseEntity<UserDTO> getByEmail(@Valid @PathVariable String email) {
    UserEntity userEntity = userServices.findByEmail(email);
    UserDTO user = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @DeleteMapping("/{metroCardNumber}")
  public ResponseEntity<String> deleteByMetroCardNumber(@Valid @PathVariable String metroCardNumber) {
    userServices.deleteByMetroCardNumber(metroCardNumber);

    return new ResponseEntity<>("Successfully deleted user", HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<String> deleteAll() {
    userServices.deleteAll();

    return new ResponseEntity<>("Successfully deleted all user", HttpStatus.OK);
  }
}
