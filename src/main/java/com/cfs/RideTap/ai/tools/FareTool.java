package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.repository.StationRepository;
import com.cfs.RideTap.station.service.implementations.MultiCityMetroNetworkSeeder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FareTool {

    @Autowired
    private FareCalculator fareCalculator;

    @Autowired
    private StationTool stationTool;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private MultiCityMetroNetworkSeeder networkSeeder;

    private String suggestSimilar(String input) {
        String norm = StationTool.normalizeQuery(input);
        List<String> suggestions = stationRepository.findAll().stream()
                .map(Station::getName)
                .filter(name -> StationTool.normalizeQuery(name).contains(norm))
                .limit(5)
                .toList();
        return suggestions.isEmpty() ? "" : " Did you mean: " + String.join(", ", suggestions) + "?";
    }

    @Tool(
            description = """
    Calculate the metro ticket fare, route summary, and step-by-step station-by-station interchange directions between an origin station and a destination station.
    Use this tool whenever the user asks about fare, ticket cost, travel price, or how to travel/reach between two stations.
    """
    )
    public String calculateFare(String originStation, String destinationStation) {
        if (originStation == null || destinationStation == null) {
            return "Both origin and destination stations are required to calculate fare.";
        }

        Optional<Station> originOpt = stationTool.resolveStation(originStation);
        Optional<Station> destOpt = stationTool.resolveStation(destinationStation);

        if (originOpt.isEmpty()) {
            return "Origin station '" + originStation + "' was not found in the RideTap network." + suggestSimilar(originStation);
        }
        if (destOpt.isEmpty()) {
            return "Destination station '" + destinationStation + "' was not found in the RideTap network." + suggestSimilar(destinationStation);
        }

        Station origin = originOpt.get();
        Station dest = destOpt.get();

        if (origin.getName().equalsIgnoreCase(dest.getName())) {
            return "Origin and destination stations must be different.";
        }

        double fare = fareCalculator.calculate(origin.getName(), dest.getName());

        // Get detailed step-by-step route with transfer stations
        String routeDirections = findDetailedRoutePath(origin.getName(), dest.getName());

        String originLines = (origin.getLines() != null && !origin.getLines().isEmpty())
                ? String.join(", ", origin.getLines())
                : "N/A";
        String destLines = (dest.getLines() != null && !dest.getLines().isEmpty())
                ? String.join(", ", dest.getLines())
                : "N/A";

        return String.format(
                "Route: %s (%s) -> %s (%s) | Route Directions: %s | Ticket Fare: ₹%.2f | Pass Validity: 3 Hours from booking",
                origin.getName(), originLines, dest.getName(), destLines, routeDirections, fare
        );
    }

    private String findDetailedRoutePath(String startName, String targetName) {
        Map<String, List<MultiCityMetroNetworkSeeder.Edge>> graph = networkSeeder.getAdjacencyGraph();
        String startKey = MultiCityMetroNetworkSeeder.normalize(startName);
        String targetKey = MultiCityMetroNetworkSeeder.normalize(targetName);

        if (!graph.containsKey(startKey) || !graph.containsKey(targetKey)) {
            return "Direct path info unavailable.";
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentStation = new HashMap<>();
        Map<String, String> edgeLineUsed = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(startKey);
        visited.add(startKey);

        boolean found = false;
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            if (curr.equals(targetKey)) {
                found = true;
                break;
            }

            for (MultiCityMetroNetworkSeeder.Edge edge : graph.getOrDefault(curr, Collections.emptyList())) {
                String nextKey = MultiCityMetroNetworkSeeder.normalize(edge.getToStation());
                if (!visited.contains(nextKey)) {
                    visited.add(nextKey);
                    parentStation.put(nextKey, curr);
                    edgeLineUsed.put(nextKey, edge.getLine());
                    queue.add(nextKey);
                }
            }
        }

        if (!found) return "No valid route found.";

        // Reconstruct full station sequence backwards
        List<String> stationPath = new ArrayList<>();
        List<String> linePath = new ArrayList<>();
        String curr = targetKey;

        // Map normalized key back to proper Capitalized station names from DB
        Map<String, String> properNames = new HashMap<>();
        stationRepository.findAll().forEach(s -> properNames.put(MultiCityMetroNetworkSeeder.normalize(s.getName()), s.getName()));

        stationPath.add(0, properNames.getOrDefault(curr, curr));

        while (parentStation.containsKey(curr)) {
            String prev = parentStation.get(curr);
            String line = edgeLineUsed.get(curr);
            linePath.add(0, line);
            curr = prev;
            stationPath.add(0, properNames.getOrDefault(curr, curr));
        }

        // Collapse into readable instructions highlighting changes
        StringBuilder summary = new StringBuilder();
        String currentLine = null;
        String changeStation = stationPath.get(0);

        summary.append("Start at ").append(changeStation);

        for (int i = 0; i < linePath.size(); i++) {
            String line = linePath.get(i);
            String reachedStation = stationPath.get(i + 1);

            if (currentLine == null) {
                currentLine = line;
                summary.append(" via ").append(line);
            } else if (!currentLine.equals(line)) {
                summary.append(" -> Change at **").append(changeStation).append("** to ").append(line);
                currentLine = line;
            }
            changeStation = reachedStation;
        }
        summary.append(" (Arrive at ").append(stationPath.get(stationPath.size() - 1)).append(")");

        return summary.toString();
    }
}