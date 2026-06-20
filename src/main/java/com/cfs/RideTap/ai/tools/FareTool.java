package com.cfs.RideTap.ai.tools;

import com.cfs.RideTap.services.interfaces.FareCalculator;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FareTool {

    @Autowired
    private FareCalculator fareCalculator;

    @Tool(
            description = """
    Calculate fare between two metro stations.
    Use this tool whenever the user asks about fare,
    ticket cost, travel cost, journey cost, or price
    between stations.
    """
    )
    public double calculateFare(String originStation, String destinationStation) {
        return fareCalculator.calculate(originStation, destinationStation);
    }
}
