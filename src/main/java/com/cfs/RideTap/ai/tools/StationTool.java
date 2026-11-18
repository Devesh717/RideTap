package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.repository.StationRepository;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StationTool {

    private static final Comparator<Station> BY_SEQUENCE =
            Comparator.comparing(Station::getSequenceNumber, Comparator.nullsLast(Comparator.naturalOrder()));

    @Autowired
    private StationServices stationServices;

    @Autowired
    private StationRepository stationRepository;

    private String formatStation(Station s) {
        String lines = (s.getLines() != null && !s.getLines().isEmpty())
                ? String.join(", ", s.getLines())
                : (s.getLineColors() != null && !s.getLineColors().isEmpty())
                ? String.join(", ", s.getLineColors())
                : "N/A";
        String interchange = s.isInterchange() ? "Yes (Interchange Hub)" : "No";
        String seq = s.getSequenceNumber() != null ? "#" + s.getSequenceNumber() + " " : "";
        return String.format("%s%s | Lines: %s | Interchange: %s",
                seq, s.getName(), lines, interchange);
    }

    public static String normalizeQuery(String raw) {
        if (raw == null) return "";
        return raw.trim()
                .replaceAll("\\s+", " ")
                // Convert common Roman numeral suffixes (e.g. "Delta I" -> "Delta 1", "Phase II" -> "Phase 2")
                .replaceAll("(?i)\\bIII\\b", "3")
                .replaceAll("(?i)\\bII\\b", "2")
                .replaceAll("(?i)\\bI\\b$", "1")
                .toLowerCase();
    }

    public Optional<Station> resolveStation(String query) {
        if (query == null || query.isBlank()) return Optional.empty();

        Optional<Station> exact = stationServices.findByName(query.trim());
        if (exact.isPresent()) return exact;

        String norm = normalizeQuery(query);
        List<Station> all = stationRepository.findAll();

        // 1. Normalized exact match (handles case, extra spaces, Roman numerals like "Delta I" -> "Delta 1")
        Optional<Station> normExact = all.stream()
                .filter(s -> normalizeQuery(s.getName()).equals(norm))
                .findFirst();
        if (normExact.isPresent()) return normExact;

        // 2. Partial contains match if unambiguous (single match)
        List<Station> partial = all.stream()
                .filter(s -> normalizeQuery(s.getName()).contains(norm))
                .toList();
        if (partial.size() == 1) {
            return Optional.of(partial.get(0));
        }
        return Optional.empty();
    }

    @Tool(
            description = """
    List all metro stations available in the RideTap network, including sequence numbers,
    metro lines, and interchange status. Use whenever the user asks to list all stations,
    see the metro network, or explore available stops.
    """
    )
    public String getAllStations() {
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            return "No metro stations are currently configured in the network.";
        }
        return stations.stream()
                .sorted(BY_SEQUENCE)
                .map(this::formatStation)
                .collect(Collectors.joining("\n"));
    }

    @Tool(
            description = """
    Get detailed information about a specific metro station by name (lines, sequence number, interchange status).
    Supports case-insensitive and partial matching.
    """
    )
    public String getStationInfo(String stationName) {
        if (stationName == null || stationName.isBlank()) {
            return "Please provide a metro station name.";
        }
        return resolveStation(stationName)
                .map(this::formatStation)
                .orElseGet(() -> {
                    String norm = normalizeQuery(stationName);
                    List<Station> matches = stationRepository.findAll().stream()
                            .filter(s -> normalizeQuery(s.getName()).contains(norm))
                            .sorted(BY_SEQUENCE)
                            .toList();
                    if (matches.isEmpty()) {
                        return "Station '" + stationName + "' does not exist in the RideTap network.";
                    }
                    return "Closest matching stations:\n" + matches.stream()
                            .map(this::formatStation)
                            .collect(Collectors.joining("\n"));
                });
    }

    @Tool(
            description = """
    List all interchange metro stations where commuters can switch between different metro lines.
    """
    )
    public String getInterchangeStations() {
        List<Station> interchanges = stationRepository.findAll().stream()
                .filter(Station::isInterchange)
                .sorted(BY_SEQUENCE)
                .toList();
        if (interchanges.isEmpty()) {
            return "No interchange stations found.";
        }
        return interchanges.stream()
                .map(this::formatStation)
                .collect(Collectors.joining("\n"));
    }

    @Tool(
            description = """
    List all metro stations belonging to a specific metro line (e.g., Blue Line, Yellow Line, Red Line, Violet Line, Pink Line, Magenta Line, Green Line, Orange Line, Grey Line, Aqua Line, Rapid Metro).
    """
    )
    public String getStationsByLine(String lineName) {
        if (lineName == null || lineName.isBlank()) {
            return "Please specify a metro line name or color.";
        }
        String target = lineName.trim().toLowerCase();
        List<Station> lineStations = stationRepository.findAll().stream()
                .filter(s -> s.getLines() != null &&
                        s.getLines().stream().anyMatch(l -> l.toLowerCase().contains(target)))
                .sorted(BY_SEQUENCE)
                .toList();
        if (lineStations.isEmpty()) {
            return "No stations found for line: " + lineName;
        }
        return lineStations.stream()
                .map(this::formatStation)
                .collect(Collectors.joining("\n"));
    }
}