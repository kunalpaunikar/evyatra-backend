package com.evyatra.service;

import com.evyatra.model.EvStation;
import com.evyatra.repository.EvStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvStationService {

    private final EvStationRepository stationRepository;

    // All active stations
    public List<EvStation> getAllActiveStations() {
        return stationRepository.findByStatus(EvStation.Status.ACTIVE);
    }

    // Search By City
    public List<EvStation> getStationsByCity(String city) {
        return stationRepository.findByCityIgnoreCaseAndStatus(city, EvStation.Status.ACTIVE);
    }

    // Search by ID single station
    public EvStation getStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station nahi mila ID: " + id));
    }

    // Admin — Add new Station
    public EvStation addStation(EvStation station) {
        return stationRepository.save(station);
    }
}