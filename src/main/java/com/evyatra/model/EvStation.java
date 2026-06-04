package com.evyatra.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ev_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private Double latitude;
    private Double longitude;

    private Integer totalChargers;
    private Integer availableChargers;

    @Column(nullable = false)
    private Double pricePerUnit;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        ACTIVE, INACTIVE
    }
}