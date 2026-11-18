package com.cfs.RideTap.admin.service.interfaces;

import com.cfs.RideTap.admin.dto.response.AdminDashboardSummaryDTO;
import com.cfs.RideTap.admin.dto.response.RouteAnalyticsDTO;
import com.cfs.RideTap.admin.dto.response.StationTrafficDTO;
import com.cfs.RideTap.user.dto.response.UserDTO;

import java.util.List;

public interface AdminDashboardServices {
  AdminDashboardSummaryDTO getSystemSummary();

  List<StationTrafficDTO> getStationTrafficAnalytics();

  List<RouteAnalyticsDTO> getTopRoutes(int limit);

  UserDTO creditUserWallet(String metroCardNumber, Double amount);

  UserDTO promoteToAdmin(String metroCardNumber);
}
