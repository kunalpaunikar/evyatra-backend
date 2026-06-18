package com.evyatra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long stationId;
    private String stationName;
    private String slotNumber;
    private String chargerType;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalAmount;
    private String status;
}