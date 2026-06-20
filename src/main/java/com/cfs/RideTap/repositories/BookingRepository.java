package com.cfs.RideTap.repositories;

import com.cfs.RideTap.enums.BookingStatus;
import com.cfs.RideTap.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

  public List<Booking> findByBookingStatus(BookingStatus bookingStatus);

  Optional<Booking> findByIdAndUser_Email(Long bookingId, String email);

  Optional<Booking> findTopByUser_EmailAndOriginStation_NameAndDestinationStation_NameOrderByDateTimeDesc(String email,
                                                                                                          String originStation,
                                                                                                          String destinationStation);

  public List<Booking> findByUserMetroCardNumber(String userMetroCardNumber);

  public List<Booking> findByUserMetroCardNumberAndBookingStatus(String userMetroCardNumber, BookingStatus bookingStatus);
}
