package com.cfs.RideTap.user.controller;

import com.cfs.RideTap.user.api.UserApi;
import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.mapper.UserDTOMapper;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController implements UserApi {
  @Autowired
  private UserServices userServices;

  @Override
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAll() {
    List<UserEntity> userEntities = userServices.findAll();

    List<UserDTO> users = userEntities
        .stream()
        .map(UserDTOMapper::map)
        .collect(Collectors.toList());

    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @Override
  @GetMapping("metro-card/{metroCardNumber}")
  public ResponseEntity<UserDTO> getByMetroCardNumber(@Valid @PathVariable String metroCardNumber) {
    UserEntity userEntity = userServices.findByMetroCardNumber(metroCardNumber);
    UserDTO user = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Override
  @GetMapping("email/{email}")
  public ResponseEntity<UserDTO> getByEmail(@Valid @PathVariable String email) {
    UserEntity userEntity = userServices.findByEmail(email);
    UserDTO user = UserDTOMapper.map(userEntity);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Override
  @DeleteMapping("/{metroCardNumber}")
  public ResponseEntity<String> deleteByMetroCardNumber(@Valid @PathVariable String metroCardNumber) {
    userServices.deleteByMetroCardNumber(metroCardNumber);

    return new ResponseEntity<>("Successfully deleted user", HttpStatus.OK);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<String> deleteAll() {
    userServices.deleteAll();

    return new ResponseEntity<>("Successfully deleted all user", HttpStatus.OK);
  }
}
