package com.evento.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                System.out.println("Firebase already initialized.");
                return;
            }

            InputStream serviceAccount;
            String envCredentials = System.getenv("FIREBASE_CREDENTIALS");

            if (envCredentials != null && !envCredentials.isBlank()) {
                serviceAccount = new ByteArrayInputStream(envCredentials.getBytes());
                System.out.println("Initializing Firebase from Environment Variables...");
            } else {
                ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
                if (!resource.exists()) {
                    System.err.println("=================================================");
                    System.err.println(" ERROR: serviceAccountKey.json NOT FOUND!");
                    System.err.println("Place it in: src/main/resources/serviceAccountKey.json");
                    System.err.println("=================================================");
                    return;
                }
                serviceAccount = resource.getInputStream();
                System.out.println("Initializing Firebase from local serviceAccountKey.json...");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("=========================================");
            System.out.println(" FIREBASE FIRESTORE CONNECTED SUCCESSFULLY!");
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("=========================================");
            System.err.println("FIREBASE INITIALIZATION FAILED:");
            e.printStackTrace();
            System.err.println("=========================================");
        }
    }
}