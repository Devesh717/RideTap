package com.cfs.RideTap.booking.api;

import java.util.List;

import com.cfs.RideTap.booking.dto.response.BookingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Booking History", description = "User Booking History Endpoints")
public interface BookingHistoryApi {

  @Operation(
          summary = "Admin can get Booking History of All Users",
          description = "Admin can fetch All Bookings of all users and by applying filters (User MetroCardNumber, Booking Status)"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched all the requested bookings"),
          @ApiResponse(responseCode = "401", description = "Not Authorized to access this resource")
  })
  ResponseEntity<List<BookingDTO>> getAllBookings(
          @RequestParam(required = false) String metroCardNumber,
          @RequestParam(required = false) Integer bookingStatus);

  @Operation(
          summary = "Get Booking History of current User",
          description = "User can fetch All Bookings of user and by applying filters (Booking Status)"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched all the requested bookings"),
          @ApiResponse(responseCode = "401", description = "User not authenticated")
  })
  ResponseEntity<List<BookingDTO>> getAllBookingsOfUser(
          @RequestParam(required = false) Integer bookingStatus,
          Authentication authentication);

  @Operation(
          summary = "Get Booking by Booking Id",
          description = "User/Admin can fetch booking by id if authorized"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched booking details"),
          @ApiResponse(responseCode = "403", description = "Not Authorized to access this resource")
  })
  ResponseEntity<BookingDTO> getById(
          @Valid @PathVariable Long id,
          Authentication authentication);
}