package com.evyatra.repository;

import com.evyatra.model.ChargerSlot;
import com.evyatra.model.EvStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChargerSlotRepository extends JpaRepository<ChargerSlot, Long> {

    // All slot of Station
    List<ChargerSlot> findByStation(EvStation station);

    // available slots of station
    List<ChargerSlot> findByStationAndStatus(EvStation station, ChargerSlot.SlotStatus status);
}