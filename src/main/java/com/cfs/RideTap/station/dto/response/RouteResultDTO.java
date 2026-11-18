package com.cfs.RideTap.station.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResultDTO {
  private String originStation;
  private String destinationStation;
  private int totalStops;
  private int interchanges;
  private double estimatedMinutes;
  private double fare;
  private List<String> linesUsed;
  private List<RouteStepDTO> path;
}
