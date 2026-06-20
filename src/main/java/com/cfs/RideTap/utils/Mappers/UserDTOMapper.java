package com.cfs.RideTap.utils.Mappers;

import com.cfs.RideTap.dtos.bookingDTO.BookingBasicDTO;
import com.cfs.RideTap.dtos.userDTO.UserDTO;
import com.cfs.RideTap.models.UserEntity;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;



public class UserDTOMapper {
  private static ModelMapper mapper = new ModelMapper();

  public static UserDTO map(UserEntity user) {
    UserDTO userDTO = mapper.map(user, UserDTO.class);

    List<BookingBasicDTO> bookingBasicDTO = userDTO.getBookings()
        .stream()
        .map(booking -> mapper.map(booking, BookingBasicDTO.class))
        .collect(Collectors.toList());

    userDTO.setBookings(bookingBasicDTO);

    return mapper.map(user, UserDTO.class);
  }
}
