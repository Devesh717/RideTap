package com.cfs.RideTap.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationTrafficDTO {
  private Long stationId;
  private String stationName;
  private Long sequenceNumber;
  private long originCount;
  private long destinationCount;
  private long totalFootfall;
}
