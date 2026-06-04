package com.evyatra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminBookingResponse {
    private Long bookingId;
    private String userName;
    private String userEmail;
    private String stationName;
    private String city;
    private String slotNumber;
    private String bookingDate;
    private String startTime;
    private String endTime;
    private Double amount;
    private String status;
    private String createdAt;
}