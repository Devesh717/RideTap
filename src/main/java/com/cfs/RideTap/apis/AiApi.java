package com.cfs.RideTap.apis;

import com.cfs.RideTap.dtos.aiDTO.AiRequestDTO;
import com.cfs.RideTap.dtos.aiDTO.AiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "RideTap AI Assistant",
        description = "AI-powered metro booking and support assistant"
)
public interface AiApi {

    @Operation(
            summary = "Chat with RideTap AI",
            description = """
                    Interact with the RideTap AI assistant.

                    Examples:
                    - Calculate fare between stations
                    - Find station information
                    - Book metro tickets
                    - View booking history
                    - Get travel assistance
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI response generated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "AI service unavailable"
            )
    })
    ResponseEntity<AiResponseDTO> chat(
            @RequestBody AiRequestDTO aiRequestDTO
    );
}