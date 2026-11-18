package com.cfs.RideTap.booking.repository;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

    Optional<Booking> findByIdAndUser_Email(Long bookingId, String email);

    Optional<Booking> findTopByUser_EmailAndOriginStation_NameAndDestinationStation_NameOrderByDateTimeDesc(
            String email,
            String originStation,
            String destinationStation);

    List<Booking> findByUserMetroCardNumber(String userMetroCardNumber);

    List<Booking> findByUserMetroCardNumberAndBookingStatus(
            String userMetroCardNumber,
            BookingStatus bookingStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.bookingStatus = :expiredStatus " +
           "WHERE b.bookingStatus = :confirmedStatus AND b.expiresAt < :now")
    int markExpiredBookings(
            @Param("confirmedStatus") BookingStatus confirmedStatus,
            @Param("expiredStatus") BookingStatus expiredStatus,
            @Param("now") LocalDateTime now);
}
