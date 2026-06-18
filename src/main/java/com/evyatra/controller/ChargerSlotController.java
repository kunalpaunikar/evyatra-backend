package com.evyatra.controller;

import com.evyatra.model.ChargerSlot;
import com.evyatra.model.EvStation;
import com.evyatra.repository.ChargerSlotRepository;
import com.evyatra.repository.EvStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChargerSlotController {

    private final ChargerSlotRepository slotRepository;
    private final EvStationRepository stationRepository;

    // Show Available slot of Station
    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<ChargerSlot>> getAvailableSlots(
            @PathVariable Long stationId) {

        EvStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found !"));

        List<ChargerSlot> slots = slotRepository
                .findByStationAndStatus(station, ChargerSlot.SlotStatus.AVAILABLE);

        return ResponseEntity.ok(slots);
    }
}