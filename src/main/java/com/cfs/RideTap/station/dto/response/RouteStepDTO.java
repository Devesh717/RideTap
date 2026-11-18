package com.cfs.RideTap.station.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStepDTO {
  private Long stationId;
  private String stationName;
  private String line;
  private String lineColor;
  private boolean interchangeHere;
  private Double latitude;
  private Double longitude;
}
