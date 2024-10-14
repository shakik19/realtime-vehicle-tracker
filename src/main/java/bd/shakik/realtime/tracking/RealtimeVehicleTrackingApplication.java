package bd.shakik.realtime.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class RealtimeVehicleTrackingApplication {
	public static void main(String[] args) {
		SpringApplication.run(RealtimeVehicleTrackingApplication.class, args);
	}
}
