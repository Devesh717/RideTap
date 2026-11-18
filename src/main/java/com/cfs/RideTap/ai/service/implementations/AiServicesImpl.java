package com.cfs.RideTap.ai.service.implementations;

import com.cfs.RideTap.ai.SystemPrompts;
import com.cfs.RideTap.ai.service.interfaces.AiServices;
import com.cfs.RideTap.ai.tools.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiServicesImpl implements AiServices {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private FareTool fareTool;

    @Autowired
    private BalanceTool balanceTool;

    @Autowired
    private BookingTool bookingTool;

    @Autowired
    private BookingHistoryTool bookingHistoryTool;

    @Autowired
    private QRTool qrTool;

    @Autowired
    private UserTool userTool;

    @Autowired
    private StationTool stationTool;

    @Override
    public String chat(String message) {
        try {
            return chatClient.prompt()
                    .system(SystemPrompts.RIDETAP_ASSISTANT)
                    .user(message)
                    .tools(fareTool,
                            balanceTool,
                            bookingTool,
                            bookingHistoryTool,
                            qrTool,
                            userTool,
                            stationTool)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
