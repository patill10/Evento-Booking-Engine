package com.evento.controller;

import com.evento.dto.BookingRequest;
import com.evento.dto.SeatHoldRequest;
import com.evento.model.Booking;
import com.evento.service.ReservationEngineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class BookingController {

    private final ReservationEngineService reservationService;

    public BookingController(ReservationEngineService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/hold")
    public ResponseEntity<String> holdSeats(@Valid @RequestBody SeatHoldRequest request) throws Exception {
        String result = reservationService.holdSeats(request);
        if (result.startsWith("Error")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@Valid @RequestBody BookingRequest request) {
        try {
            Booking booking = reservationService.confirmBooking(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}