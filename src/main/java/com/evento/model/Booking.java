package com.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String bookingId;
    private String eventId;
    private String eventTitle;
    private String userId;
    private String userEmail;
    private List<String> seatIds;
    private double totalAmount;
    private String bookingTimestamp;
    private String qrCodeBase64; // PNG image as Base64 string
    private String status;       // "CONFIRMED", "CANCELLED"
}