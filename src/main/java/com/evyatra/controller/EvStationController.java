package com.evyatra.controller;

import com.evyatra.model.EvStation;
import com.evyatra.service.EvStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EvStationController {

    private final EvStationService stationService;

    // All stations — Any one can saw (no token needed)
    @GetMapping
    public ResponseEntity<List<EvStation>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllActiveStations());
    }

    // search by city — anyone
    @GetMapping("/search")
    public ResponseEntity<List<EvStation>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(stationService.getStationsByCity(city));
    }

    // Single station by ID
    @GetMapping("/{id}")
    public ResponseEntity<EvStation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    // Only Admin can add the Station.
    @PostMapping("/admin/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EvStation> addStation(@RequestBody EvStation station) {
        return ResponseEntity.ok(stationService.addStation(station));
    }
}