package com.cfs.RideTap.controllers;

import com.cfs.RideTap.apis.AiApi;
import com.cfs.RideTap.dtos.aiDTO.AiRequestDTO;
import com.cfs.RideTap.dtos.aiDTO.AiResponseDTO;
import com.cfs.RideTap.services.interfaces.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController implements AiApi {

    @Autowired
    private AiServices aiServices;

    @PostMapping
    public ResponseEntity<AiResponseDTO> chat(@RequestBody AiRequestDTO aiRequestDTO) {
        String response = aiServices.chat(aiRequestDTO.message());

        return ResponseEntity.ok(new AiResponseDTO(response));
    }
}
