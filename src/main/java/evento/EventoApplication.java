package evento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"evento", "com.evento"})
public class EventoApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventoApplication.class, args);
    }
}