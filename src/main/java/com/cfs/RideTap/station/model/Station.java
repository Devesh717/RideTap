package com.cfs.RideTap.station.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "stations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"city", "name"})
        }
)
public class Station {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String city = "Delhi NCR"; // Default city fallback

  @Column(nullable = true)
  private Long sequenceNumber;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "station_lines", joinColumns = @JoinColumn(name = "station_id"))
  @Column(name = "line_name")
  private List<String> lines = new ArrayList<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "station_line_colors", joinColumns = @JoinColumn(name = "station_id"))
  @Column(name = "line_color")
  private List<String> lineColors = new ArrayList<>();

  @Column(nullable = false)
  private boolean interchange = false;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;
}