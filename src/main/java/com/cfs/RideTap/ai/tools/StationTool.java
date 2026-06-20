package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.models.Station;
import com.cfs.RideTap.services.interfaces.StationServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StationTool {

    @Autowired
    private StationServices stationServices;

    @Tool(
            description = """
    Get information about a metro station.
    Use this tool when the user asks whether a station exists,
    requests station details, station information,
    or searches for a station by name.
    """
    )    public String getStationInfo(String stationName) {

        return stationServices.findByName(stationName)
                .map(station ->
                        "Station Name: "
                                + station.getName())
                .orElse("Station does not exist");
    }
}
