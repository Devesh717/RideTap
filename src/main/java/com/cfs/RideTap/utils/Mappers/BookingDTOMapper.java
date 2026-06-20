package com.cfs.RideTap.utils.Mappers;

import com.cfs.RideTap.dtos.bookingDTO.BookingDTO;
import com.cfs.RideTap.dtos.userDTO.UserBasicDto;
import com.cfs.RideTap.models.Booking;
import org.modelmapper.ModelMapper;


public class BookingDTOMapper {
  private static ModelMapper mapper = new ModelMapper();

  public static BookingDTO map(Booking booking) {
    BookingDTO bookingResponse = mapper.map(booking, BookingDTO.class);

    UserBasicDto user = mapper.map(booking.getUser(), UserBasicDto.class);
    bookingResponse.setUser(user);

    return mapper.map(booking, BookingDTO.class);
  }
}
