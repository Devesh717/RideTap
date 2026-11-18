package com.cfs.RideTap.booking.service.implementations;

import java.time.LocalDateTime;
import java.util.List;

import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.repository.BookingRepository;
import com.cfs.RideTap.booking.service.interfaces.BookingHistoryServices;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingHistoryServicesImpl implements BookingHistoryServices {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserServices userServices;

    private void syncExpiredStatuses() {
        bookingRepository.markExpiredBookings(
                BookingStatus.CONFIRMED,
                BookingStatus.EXPIRED,
                LocalDateTime.now());
    }

    @Override
    @Transactional
    public List<Booking> findAllBookings() {
        syncExpiredStatuses();
        return bookingRepository.findAll();
    }

    @Override
    @Transactional
    public List<Booking> findAllBookingsByBookingStatus(BookingStatus bookingStatus) {
        syncExpiredStatuses();
        return bookingRepository.findByBookingStatus(bookingStatus);
    }

    @Override
    @Transactional
    public List<Booking> findAllUserBookings(String metroCardNumber) {
        if (!userServices.existsByMetroCardNumber(metroCardNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User by metroCardNumber " + metroCardNumber + " does not exist");
        }
        syncExpiredStatuses();
        return bookingRepository.findByUserMetroCardNumber(metroCardNumber);
    }

    @Override
    @Transactional
    public List<Booking> findByMetroCardNumberAndBookingStatus(String metroCardNumber, BookingStatus bookingStatus) {
        if (!userServices.existsByMetroCardNumber(metroCardNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User by metroCardNumber " + metroCardNumber + " does not exist");
        }
        syncExpiredStatuses();
        return bookingRepository.findByUserMetroCardNumberAndBookingStatus(metroCardNumber, bookingStatus);
    }

    @Override
    @Transactional
    public Booking findById(String metroCardNumber, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!booking.getUser().getMetroCardNumber().equals(metroCardNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (booking.isExpired() && booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            booking = bookingRepository.save(booking);
        }

        return booking;
    }
}
