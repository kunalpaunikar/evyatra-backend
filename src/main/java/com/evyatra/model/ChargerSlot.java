package com.evyatra.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "charger_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargerSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne                          // Many slots → One station
    @JoinColumn(name = "station_id")
    private EvStation station;

    private String slotNumber;

    @Enumerated(EnumType.STRING)
    private ChargerType chargerType;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.AVAILABLE;

    public enum ChargerType {
        AC_SLOW, DC_FAST, SUPERFAST
    }

    public enum SlotStatus {
        AVAILABLE, BOOKED, MAINTENANCE
    }
}