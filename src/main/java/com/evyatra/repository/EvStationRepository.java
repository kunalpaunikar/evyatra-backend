
package com.evyatra.repository;

import com.evyatra.model.EvStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvStationRepository extends JpaRepository<EvStation, Long> {

    // Search stations by city
    // SQL: SELECT * FROM ev_stations WHERE city = ?
    List<EvStation> findByCityIgnoreCase(String city);

    // Active stations
    // SQL: SELECT * FROM ev_stations WHERE status = 'ACTIVE'
    List<EvStation> findByStatus(EvStation.Status status);

    // City + active stations
    List<EvStation> findByCityIgnoreCaseAndStatus(String city, EvStation.Status status);
}