package com.cfs.RideTap.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryDTO {
  private long totalUsers;
  private long totalStations;
  private long totalBookings;
  private long confirmedBookings;
  private long cancelledBookings;
  private double totalConfirmedRevenue;
  private double totalRefundedAmount;
  private double totalWalletBalanceHeld;
}
