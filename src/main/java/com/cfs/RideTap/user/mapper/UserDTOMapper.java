package com.cfs.RideTap.user.mapper;

import com.cfs.RideTap.booking.dto.response.BookingBasicDTO;
import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.model.UserEntity;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTOMapper {
  private static final ModelMapper mapper = new ModelMapper();

  public static UserDTO map(UserEntity user) {
    UserDTO userDTO = mapper.map(user, UserDTO.class);

    if (user.getBookings() != null) {
      List<BookingBasicDTO> bookingBasicDTO = user.getBookings()
          .stream()
          .map(booking -> mapper.map(booking, BookingBasicDTO.class))
          .collect(Collectors.toList());
      userDTO.setBookings(bookingBasicDTO);
    } else {
      userDTO.setBookings(Collections.emptyList());
    }

    return userDTO;
  }
}
