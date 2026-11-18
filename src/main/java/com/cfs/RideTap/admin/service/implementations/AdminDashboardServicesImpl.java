package com.cfs.RideTap.admin.service.implementations;

import com.cfs.RideTap.admin.dto.response.AdminDashboardSummaryDTO;
import com.cfs.RideTap.admin.dto.response.RouteAnalyticsDTO;
import com.cfs.RideTap.admin.dto.response.StationTrafficDTO;
import com.cfs.RideTap.admin.service.interfaces.AdminDashboardServices;
import com.cfs.RideTap.balance.service.interfaces.BalanceServices;
import com.cfs.RideTap.booking.enums.BookingStatus;
import com.cfs.RideTap.booking.model.Booking;
import com.cfs.RideTap.booking.service.interfaces.BookingHistoryServices;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import com.cfs.RideTap.user.dto.response.UserDTO;
import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.mapper.UserDTOMapper;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServicesImpl implements AdminDashboardServices {

  @Autowired
  private UserServices userServices;

  @Autowired
  private StationServices stationServices;

  @Autowired
  private BookingHistoryServices bookingHistoryServices;

  @Autowired
  private BalanceServices balanceServices;

  @Override
  @Transactional(readOnly = true)
  public AdminDashboardSummaryDTO getSystemSummary() {
    List<UserEntity> users = userServices.findAll();
    List<Station> stations = stationServices.findAll();
    List<Booking> bookings = bookingHistoryServices.findAllBookings();

    long confirmedCount = 0;
    long cancelledCount = 0;
    double confirmedRevenue = 0.0;
    double refundedAmount = 0.0;

    for (Booking booking : bookings) {
      if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
        confirmedCount++;
        confirmedRevenue += booking.getFare();
      } else if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
        cancelledCount++;
        refundedAmount += booking.getFare();
      }
    }

    double totalWalletHeld = users.stream()
        .mapToDouble(UserEntity::getBalance)
        .sum();

    return AdminDashboardSummaryDTO.builder()
        .totalUsers(users.size())
        .totalStations(stations.size())
        .totalBookings(bookings.size())
        .confirmedBookings(confirmedCount)
        .cancelledBookings(cancelledCount)
        .totalConfirmedRevenue(Math.round(confirmedRevenue * 100.0) / 100.0)
        .totalRefundedAmount(Math.round(refundedAmount * 100.0) / 100.0)
        .totalWalletBalanceHeld(Math.round(totalWalletHeld * 100.0) / 100.0)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<StationTrafficDTO> getStationTrafficAnalytics() {
    List<Station> stations = stationServices.findAll();
    List<Booking> bookings = bookingHistoryServices.findAllBookings();

    Map<Long, Long> originCounts = new HashMap<>();
    Map<Long, Long> destinationCounts = new HashMap<>();

    for (Booking booking : bookings) {
      if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
        continue;
      }
      if (booking.getOriginStation() != null && booking.getOriginStation().getId() != null) {
        Long originId = booking.getOriginStation().getId();
        originCounts.put(originId, originCounts.getOrDefault(originId, 0L) + 1L);
      }
      if (booking.getDestinationStation() != null && booking.getDestinationStation().getId() != null) {
        Long destId = booking.getDestinationStation().getId();
        destinationCounts.put(destId, destinationCounts.getOrDefault(destId, 0L) + 1L);
      }
    }

    return stations.stream()
        .sorted(Comparator.comparingLong(Station::getSequenceNumber))
        .map(station -> {
          long boardings = originCounts.getOrDefault(station.getId(), 0L);
          long alightings = destinationCounts.getOrDefault(station.getId(), 0L);
          return StationTrafficDTO.builder()
              .stationId(station.getId())
              .stationName(station.getName())
              .sequenceNumber(station.getSequenceNumber())
              .originCount(boardings)
              .destinationCount(alightings)
              .totalFootfall(boardings + alightings)
              .build();
        })
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<RouteAnalyticsDTO> getTopRoutes(int limit) {
    List<Booking> bookings = bookingHistoryServices.findAllBookings();
    Map<String, RouteAnalyticsDTO> routeMap = new HashMap<>();

    for (Booking booking : bookings) {
      if (booking.getBookingStatus() != BookingStatus.CONFIRMED
          || booking.getOriginStation() == null
          || booking.getDestinationStation() == null) {
        continue;
      }

      String originName = booking.getOriginStation().getName();
      String destName = booking.getDestinationStation().getName();
      String key = originName + "->" + destName;

      RouteAnalyticsDTO existing = routeMap.get(key);
      if (existing == null) {
        routeMap.put(key, RouteAnalyticsDTO.builder()
            .originStation(originName)
            .destinationStation(destName)
            .confirmedRides(1L)
            .totalRevenue(booking.getFare())
            .build());
      } else {
        existing.setConfirmedRides(existing.getConfirmedRides() + 1L);
        existing.setTotalRevenue(
            Math.round((existing.getTotalRevenue() + booking.getFare()) * 100.0) / 100.0
        );
      }
    }

    int safeLimit = Math.max(1, Math.min(limit, 50));
    return routeMap.values().stream()
        .sorted(Comparator.comparingLong(RouteAnalyticsDTO::getConfirmedRides).reversed())
        .limit(safeLimit)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserDTO creditUserWallet(String metroCardNumber, Double amount) {
    UserEntity user = userServices.findByMetroCardNumber(metroCardNumber);
    balanceServices.addMoney(user, amount);
    UserEntity savedUser = userServices.save(user);
    return UserDTOMapper.map(savedUser);
  }

  @Override
  @Transactional
  public UserDTO promoteToAdmin(String metroCardNumber) {
    UserEntity user = userServices.findByMetroCardNumber(metroCardNumber);
    List<Role> roles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();
    if (!roles.contains(Role.ADMIN)) {
      roles.add(Role.ADMIN);
      user.setRoles(roles);
    }
    UserEntity savedUser = userServices.save(user);
    return UserDTOMapper.map(savedUser);
  }
}
