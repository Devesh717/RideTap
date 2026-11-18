package com.cfs.RideTap.booking.mapper;

import com.cfs.RideTap.booking.dto.response.BookingDTO;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.user.dto.response.UserBasicDto;
import org.modelmapper.ModelMapper;

public class BookingDTOMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static BookingDTO map(Booking booking) {
        BookingDTO dto = mapper.map(booking, BookingDTO.class);

        if (booking.getUser() != null) {
            UserBasicDto user = mapper.map(booking.getUser(), UserBasicDto.class);
            dto.setUser(user);
        }

        boolean expired = booking.isExpired();
        dto.setExpired(expired);
        if (expired && booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            dto.setBookingStatus(BookingStatus.EXPIRED);
        }

        dto.setExpiresAt(booking.getExpiresAt());
        dto.setPaymentMethod(booking.getPaymentMethod());
        dto.setRazorpayPaymentId(booking.getRazorpayPaymentId());
        dto.setQrPath(booking.getQrCodePath());
        return dto;
    }
}
