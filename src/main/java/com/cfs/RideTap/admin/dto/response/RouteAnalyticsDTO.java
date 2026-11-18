package com.cfs.RideTap.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteAnalyticsDTO {
  private String originStation;
  private String destinationStation;
  private long confirmedRides;
  private double totalRevenue;
}
