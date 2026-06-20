package com.cfs.RideTap.dtos.stationDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddStationRequest {
  @NotBlank(message = "Station name can not be blank")
  private String name;
}
