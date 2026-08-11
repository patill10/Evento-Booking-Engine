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
        String eventId = UUID.randomUUID().toString();
        event.setId(eventId);

        List<Seat> generatedSeats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};

        for (String row : rows) {
            for (int i = 1; i <= 6; i++) {
                double price = (row.equals("A") || row.equals("B")) ? 100.0 : 60.0;
                generatedSeats.add(new Seat(row + i, row, i, price, "AVAILABLE", null, null));
            }
        }
        event.setSeats(generatedSeats);

        db.collection("events").document(eventId).set(event);
        return eventId;
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