package com.cfs.RideTap.station.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddStationRequest {
  @NotBlank(message = "Station name can not be blank")
  private String name;

  @NotBlank(message = "City cannot be blank")
  private String city; // e.g., "Delhi NCR", "Mumbai", "Bengaluru", etc.

  private List<String> lines;
  private List<String> lineColors;
  private String connectToStation;
}