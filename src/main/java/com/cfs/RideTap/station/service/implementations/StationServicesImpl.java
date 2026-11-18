package com.cfs.RideTap.station.service.implementations;

import com.cfs.RideTap.station.dto.request.AddStationRequest;
import com.cfs.RideTap.station.dto.response.RouteResultDTO;
import com.cfs.RideTap.station.dto.response.RouteStepDTO;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.repository.StationRepository;
import com.cfs.RideTap.station.service.implementations.MultiCityMetroNetworkSeeder.Edge;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class StationServicesImpl implements StationServices {

  @Autowired
  private StationRepository stationRepository;

  @Autowired
  private MultiCityMetroNetworkSeeder networkSeeder;

  @Data
  @AllArgsConstructor
  private static class DijkstraNode implements Comparable<DijkstraNode> {
    private String stationName;
    private String currentLine;
    private String currentLineColor;
    private int cost;
    private int stops;
    private int interchanges;

    @Override
    public int compareTo(DijkstraNode other) {
      return Integer.compare(this.cost, other.cost);
    }
  }

  @Data
  @AllArgsConstructor
  private static class PrevHop {
    private String prevStation;
    private String prevLine;
    private String edgeLine;
    private String edgeColor;
  }

  private String resolveCity(String rawCity) {
    return (rawCity != null && !rawCity.isBlank()) ? rawCity.trim() : "Delhi NCR";
  }

  @Override
  public Station add(AddStationRequest addStationRequest) throws ResponseStatusException {
    String cleanName = addStationRequest.getName().trim();
    String city = resolveCity(addStationRequest.getCity());

    Optional<Station> existing = stationRepository.findByNameIgnoreCaseAndCityIgnoreCase(cleanName, city);
    if (existing.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Station already exists with this name in " + city);
    }

    Long maxSequence = stationRepository.findMaxSequenceNumber();
    List<String> lines = (addStationRequest.getLines() != null && !addStationRequest.getLines().isEmpty())
            ? addStationRequest.getLines()
            : List.of("Custom Extension Line");
    List<String> colors = (addStationRequest.getLineColors() != null && !addStationRequest.getLineColors().isEmpty())
            ? addStationRequest.getLineColors()
            : List.of("#0D9488");

    Station station = new Station();
    station.setName(cleanName);
    station.setCity(city);
    station.setSequenceNumber(maxSequence != null ? maxSequence + 1 : 1L);
    station.setLines(new ArrayList<>(lines));
    station.setLineColors(new ArrayList<>(colors));
    station.setInterchange(lines.size() > 1);

    Station saved = stationRepository.save(station);

    if (addStationRequest.getConnectToStation() != null && !addStationRequest.getConnectToStation().isBlank()) {
      networkSeeder.addDynamicEdge(
              city,
              cleanName,
              addStationRequest.getConnectToStation().trim(),
              lines.get(0),
              colors.get(0)
      );
    }

    return saved;
  }

  @Override
  public List<Station> findAll() {
    return stationRepository.findAllByOrderBySequenceNumberAsc();
  }

  @Override
  public List<Station> searchStations(String query, String line) {
    List<Station> base = (query != null && !query.isBlank())
            ? stationRepository.findByNameContainingIgnoreCaseOrderBySequenceNumberAsc(query.trim())
            : stationRepository.findAllByOrderBySequenceNumberAsc();

    if (line != null && !line.isBlank() && !"ALL".equalsIgnoreCase(line.trim())) {
      String targetLine = line.trim().toLowerCase(Locale.ROOT);
      return base.stream()
              .filter(s -> s.getLines() != null && s.getLines().stream()
                      .anyMatch(l -> l.toLowerCase(Locale.ROOT).contains(targetLine)))
              .collect(Collectors.toList());
    }
    return base;
  }

  @Override
  public RouteResultDTO findShortestRoute(String originName, String destinationName)
          throws ResponseStatusException {

    List<Station> originMatches = stationRepository.findByNameIgnoreCase(originName.trim());
    if (originMatches.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin station not found: " + originName);
    }
    Station origin = originMatches.get(0);

    List<Station> destMatches = stationRepository.findByNameIgnoreCase(destinationName.trim());
    if (destMatches.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination station not found: " + destinationName);
    }

    String originCity = resolveCity(origin.getCity());
    Station destination = destMatches.stream()
            .filter(d -> resolveCity(d.getCity()).equalsIgnoreCase(originCity))
            .findFirst()
            .orElse(destMatches.get(0));

    String city = originCity;
    String startName = origin.getName();
    String endName = destination.getName();

    String startKeyCity = MultiCityMetroNetworkSeeder.normalize(city + "_" + startName);
    String startKeyRaw = MultiCityMetroNetworkSeeder.normalize(startName);
    String endKeyCity = MultiCityMetroNetworkSeeder.normalize(city + "_" + endName);
    String endKeyRaw = MultiCityMetroNetworkSeeder.normalize(endName);

    Map<String, List<Edge>> graph = networkSeeder.getAdjacencyGraph();

    if (startKeyCity.equals(endKeyCity) || startKeyRaw.equals(endKeyRaw)) {
      String defaultLine = origin.getLines().isEmpty() ? "Metro Line" : origin.getLines().get(0);
      String defaultColor = origin.getLineColors().isEmpty() ? "#0D9488" : origin.getLineColors().get(0);
      return RouteResultDTO.builder()
              .originStation(startName)
              .destinationStation(endName)
              .totalStops(0)
              .interchanges(0)
              .estimatedMinutes(0)
              .fare(10.0)
              .linesUsed(List.of(defaultLine))
              .path(List.of(RouteStepDTO.builder()
                      .stationId(origin.getId())
                      .stationName(startName)
                      .line(defaultLine)
                      .lineColor(defaultColor)
                      .interchangeHere(false)
                      .build()))
              .build();
    }

    Map<String, Station> dbStationLookup = stationRepository.findAll().stream()
            .collect(Collectors.toMap(
                    s -> MultiCityMetroNetworkSeeder.normalize(resolveCity(s.getCity()) + "_" + s.getName()),
                    s -> s,
                    (a, b) -> a
            ));

    Map<String, Integer> dist = new HashMap<>();
    Map<String, PrevHop> parent = new HashMap<>();
    PriorityQueue<DijkstraNode> pq = new PriorityQueue<>();

    String initState = startKeyCity + "|START";
    dist.put(initState, 0);
    pq.offer(new DijkstraNode(startName, "START", "#0D9488", 0, 0, 0));

    String bestEndState = null;
    int bestEndCost = Integer.MAX_VALUE;

    while (!pq.isEmpty()) {
      DijkstraNode curr = pq.poll();
      String currNameNorm = MultiCityMetroNetworkSeeder.normalize(curr.getStationName());
      String currState = currNameNorm + "|" + curr.getCurrentLine();

      if (curr.getCost() > dist.getOrDefault(currState, Integer.MAX_VALUE)) {
        continue;
      }

      if (currNameNorm.equals(endKeyRaw) || currNameNorm.equals(MultiCityMetroNetworkSeeder.normalize(endName))) {
        if (curr.getCost() < bestEndCost) {
          bestEndCost = curr.getCost();
          bestEndState = currState;
        }
        break;
      }

      List<Edge> neighbors = graph.get(MultiCityMetroNetworkSeeder.normalize(city + "_" + curr.getStationName()));
      if (neighbors == null || neighbors.isEmpty()) {
        neighbors = graph.get(currNameNorm);
      }
      if (neighbors == null) {
        neighbors = Collections.emptyList();
      }

      for (Edge edge : neighbors) {
        String nextNameNorm = MultiCityMetroNetworkSeeder.normalize(edge.getToStation());
        boolean isSwitch = !"START".equals(curr.getCurrentLine())
                && !curr.getCurrentLine().equals(edge.getLine());

        int stepCost = (edge.getWeight() * 10) + (isSwitch ? 4 : 0);
        int newCost = curr.getCost() + stepCost;
        String nextState = nextNameNorm + "|" + edge.getLine();

        if (newCost < dist.getOrDefault(nextState, Integer.MAX_VALUE)) {
          dist.put(nextState, newCost);
          parent.put(nextState, new PrevHop(
                  curr.getStationName(),
                  curr.getCurrentLine(),
                  edge.getLine(),
                  edge.getLineColor()
          ));
          pq.offer(new DijkstraNode(
                  edge.getToStation(),
                  edge.getLine(),
                  edge.getLineColor(),
                  newCost,
                  curr.getStops() + 1,
                  curr.getInterchanges() + (isSwitch ? 1 : 0)
          ));
        }
      }
    }

    if (bestEndState == null) {
      log.warn("DIJKSTRA FAILED TO REACH '{}' FROM '{}'. Falling back to 2-station path. Total visited states: {}", endKeyCity, startKeyCity, dist.size());

      long fallbackStops = Math.max(1L, Math.abs(origin.getSequenceNumber() - destination.getSequenceNumber()));
      double fallbackFare = computeDMRCFare((int) fallbackStops);
      String lineA = origin.getLines().isEmpty() ? "Delhi Metro" : origin.getLines().get(0);
      String colorA = origin.getLineColors().isEmpty() ? "#0D9488" : origin.getLineColors().get(0);
      return RouteResultDTO.builder()
              .originStation(startName)
              .destinationStation(endName)
              .totalStops((int) fallbackStops)
              .interchanges(0)
              .estimatedMinutes(fallbackStops * 2.5)
              .fare(fallbackFare)
              .linesUsed(List.of(lineA))
              .path(List.of(
                      RouteStepDTO.builder()
                              .stationId(origin.getId())
                              .stationName(startName)
                              .line(lineA)
                              .lineColor(colorA)
                              .build(),
                      RouteStepDTO.builder()
                              .stationId(destination.getId())
                              .stationName(endName)
                              .line(lineA)
                              .lineColor(colorA)
                              .build()
              ))
              .build();
    }

    LinkedList<RouteStepDTO> steps = new LinkedList<>();
    String cursorState = bestEndState;
    String cursorStationName = endName;

    while (parent.containsKey(cursorState)) {
      PrevHop hop = parent.get(cursorState);
      Station stEntity = dbStationLookup.get(MultiCityMetroNetworkSeeder.normalize(city + "_" + cursorStationName));
      if (stEntity == null) {
        stEntity = dbStationLookup.get(MultiCityMetroNetworkSeeder.normalize(cursorStationName));
      }

      steps.addFirst(RouteStepDTO.builder()
              .stationId(stEntity != null ? stEntity.getId() : null)
              .stationName(stEntity != null ? stEntity.getName() : cursorStationName)
              .line(hop.getEdgeLine())
              .lineColor(hop.getEdgeColor())
              .interchangeHere(false)
              .build());

      cursorStationName = hop.getPrevStation();
      String prevNameNorm = MultiCityMetroNetworkSeeder.normalize(hop.getPrevStation());
      cursorState = prevNameNorm + "|" + hop.getPrevLine();
    }

    String firstLine = steps.isEmpty() ? "Metro Line" : steps.getFirst().getLine();
    String firstColor = steps.isEmpty() ? "#0D9488" : steps.getFirst().getLineColor();
    steps.addFirst(RouteStepDTO.builder()
            .stationId(origin.getId())
            .stationName(startName)
            .line(firstLine)
            .lineColor(firstColor)
            .interchangeHere(false)
            .build());

    List<String> linesUsed = new ArrayList<>();
    int interchanges = 0;
    for (int i = 0; i < steps.size(); i++) {
      RouteStepDTO step = steps.get(i);
      if (!linesUsed.contains(step.getLine())) {
        linesUsed.add(step.getLine());
      }
      if (i > 0 && !steps.get(i - 1).getLine().equals(step.getLine())) {
        steps.get(i - 1).setInterchangeHere(true);
        interchanges++;
      }
    }

    int totalStops = Math.max(0, steps.size() - 1);
    double fare = computeDMRCFare(totalStops);
    double estimatedMinutes = Math.round(((totalStops * 2.2) + (interchanges * 5.0)) * 10.0) / 10.0;

    return RouteResultDTO.builder()
            .originStation(startName)
            .destinationStation(endName)
            .totalStops(totalStops)
            .interchanges(interchanges)
            .estimatedMinutes(estimatedMinutes)
            .fare(fare)
            .linesUsed(linesUsed)
            .path(steps)
            .build();
  }

  public static double computeDMRCFare(int stops) {
    if (stops <= 2) return 10.0;
    if (stops <= 5) return 20.0;
    if (stops <= 12) return 30.0;
    if (stops <= 21) return 40.0;
    if (stops <= 32) return 50.0;
    return 60.0;
  }

  @Override
  public boolean existsById(Long id) {
    return stationRepository.existsById(id);
  }

  @Override
  public Station findById(Long id) throws ResponseStatusException {
    return stationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found by provided id"));
  }

  @Override
  public Optional<Station> findByName(String name) {
    if (name == null) return Optional.empty();
    List<Station> matches = stationRepository.findByNameIgnoreCase(name.trim());
    return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
  }

  @Override
  public void deleteById(Long id) throws ResponseStatusException {
    if (!stationRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found by provided id");
    }
    stationRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    stationRepository.deleteAll();
  }

  private String canonicalStationName(String rawName) {
    if (rawName == null) return "";
    String cleaned = rawName.trim();
    // Normalize common DMRC Roman numeral / hyphen station aliases
    return cleaned
            .replaceAll("(?i)\\bMayur Vihar[-\\s]*I\\b", "Mayur Vihar Phase-1")
            .replaceAll("(?i)\\bMayur Vihar[-\\s]*II\\b", "Mayur Vihar Phase-2")
            .replaceAll("(?i)\\bAshok Park Main\\b", "Ashok Park Main");
  }
}