package com.evento.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.evento.model.Event;
import com.evento.model.Seat;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {

    public String createEvent(Event event) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }

        // Generate 30 seats if empty
        if (event.getSeats() == null || event.getSeats().isEmpty()) {
            List<Seat> generatedSeats = new ArrayList<>();
            String[] rows = {"A", "B", "C", "D", "E"};

            for (String row : rows) {
                for (int i = 1; i <= 6; i++) {
                    double price = (row.equals("A") || row.equals("B")) ? 2500.0 : 1200.0;
                    generatedSeats.add(new Seat(row + i, row, i, price, "AVAILABLE", null, null));
                }
            }
            event.setSeats(generatedSeats);
        }

        // Convert Event object to Map to ensure error-free Firestore serialization
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("id", event.getId());
        eventMap.put("title", event.getTitle());
        eventMap.put("venue", event.getVenue());
        eventMap.put("date", event.getDate());

        List<Map<String, Object>> seatListMap = new ArrayList<>();
        for (Seat seat : event.getSeats()) {
            Map<String, Object> sMap = new HashMap<>();
            sMap.put("seatId", seat.getSeatId());
            sMap.put("seatRow", seat.getSeatRow());
            sMap.put("seatNumber", seat.getSeatNumber());
            sMap.put("price", seat.getPrice());
            sMap.put("status", seat.getStatus());
            sMap.put("heldByUserId", seat.getHeldByUserId());
            sMap.put("holdExpirationTime", seat.getHoldExpirationTime());
            seatListMap.add(sMap);
        }
        eventMap.put("seats", seatListMap);

        // Write map to Firestore
        db.collection("events").document(event.getId()).set(eventMap).get();
        System.out.println("🔥 Event created in Firestore with ID: " + event.getId());
        
        return event.getId();
    }

    public Event getEventById(String eventId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("events").document(eventId).get().get();
        if (!doc.exists()) throw new RuntimeException("Event not found");
        return doc.toObject(Event.class);
    }

    public List<Event> getAllEvents() throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> docs = db.collection("events").get().get().getDocuments();
        List<Event> events = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            events.add(doc.toObject(Event.class));
        }
        return events;
    }
}