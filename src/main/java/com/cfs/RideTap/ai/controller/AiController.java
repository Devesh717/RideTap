package com.cfs.RideTap.ai.controller;

import com.cfs.RideTap.ai.api.AiApi;
import com.cfs.RideTap.ai.dto.request.AiRequestDTO;
import com.cfs.RideTap.ai.dto.response.AiResponseDTO;
import com.cfs.RideTap.ai.service.interfaces.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AiController implements AiApi {

    @Autowired
    private AiServices aiServices;

    @Override
    @PostMapping
    public ResponseEntity<AiResponseDTO> chat(@RequestBody AiRequestDTO aiRequestDTO) {
        String response = aiServices.chat(aiRequestDTO.message());
        return ResponseEntity.ok(new AiResponseDTO(response));
    }
}
