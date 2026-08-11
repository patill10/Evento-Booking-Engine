package com.evento.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.evento.dto.BookingRequest;
import com.evento.dto.SeatHoldRequest;
import com.evento.model.Booking;
import com.evento.model.Event;
import com.evento.model.Seat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ReservationEngineService {

    private final QRCodeService qrCodeService;
    private static final long HOLD_DURATION_MS = 5 * 60 * 1000; // 5 Minutes

    public ReservationEngineService(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    // 1. Atomic Seat Hold (5-Minute Lock)
    public String holdSeats(SeatHoldRequest request) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference eventRef = db.collection("events").document(request.getEventId());

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef).get();
            if (!snapshot.exists()) return "Error: Event not found";

            Event event = snapshot.toObject(Event.class);
            long currentTime = System.currentTimeMillis();

            for (Seat seat : event.getSeats()) {
                if (request.getSeatIds().contains(seat.getSeatId())) {
                    boolean isExpired = "HELD".equals(seat.getStatus()) && 
                                        seat.getHoldExpirationTime() != null && 
                                        seat.getHoldExpirationTime() < currentTime;

                    if ("BOOKED".equals(seat.getStatus())) {
                        return "Error: Seat " + seat.getSeatId() + " is already booked.";
                    }
                    if ("HELD".equals(seat.getStatus()) && !isExpired && !request.getUserId().equals(seat.getHeldByUserId())) {
                        return "Error: Seat " + seat.getSeatId() + " is held by another user.";
                    }

                    seat.setStatus("HELD");
                    seat.setHeldByUserId(request.getUserId());
                    seat.setHoldExpirationTime(currentTime + HOLD_DURATION_MS);
                }
            }

            transaction.set(eventRef, event);
            return "SUCCESS: Seats held for 5 minutes.";
        }).get();
    }

    // 2. Atomic Booking Confirmation
    public Booking confirmBooking(BookingRequest request) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference eventRef = db.collection("events").document(request.getEventId());

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef).get();
            if (!snapshot.exists()) throw new RuntimeException("Event not found");

            Event event = snapshot.toObject(Event.class);
            long currentTime = System.currentTimeMillis();
            double totalAmount = 0.0;

            for (Seat seat : event.getSeats()) {
                if (request.getSeatIds().contains(seat.getSeatId())) {
                    if ("BOOKED".equals(seat.getStatus())) {
                        throw new RuntimeException("Seat " + seat.getSeatId() + " is already booked.");
                    }
                    if ("HELD".equals(seat.getStatus()) && seat.getHoldExpirationTime() != null && seat.getHoldExpirationTime() < currentTime) {
                        throw new RuntimeException("Hold expired for seat " + seat.getSeatId());
                    }

                    seat.setStatus("BOOKED");
                    seat.setHeldByUserId(null);
                    seat.setHoldExpirationTime(null);
                    totalAmount += seat.getPrice();
                }
            }

            String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String qrData = "EVENTO:" + bookingId + "|SEATS:" + String.join(",", request.getSeatIds());
            String qrBase64 = qrCodeService.generateQRCodeBase64(qrData, 250, 250);

            Booking booking = new Booking(
                    bookingId, event.getId(), event.getTitle(), request.getUserId(),
                    request.getUserEmail(), request.getSeatIds(), totalAmount,
                    Instant.now().toString(), qrBase64, "CONFIRMED"
            );

            DocumentReference bookingRef = db.collection("bookings").document(bookingId);
            transaction.set(eventRef, event);
            transaction.set(bookingRef, booking);

            return booking;
        }).get();
    }
}