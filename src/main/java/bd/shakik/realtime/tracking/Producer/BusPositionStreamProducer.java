package bd.shakik.realtime.tracking.Producer;

import bd.shakik.avro.schemas.BusPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BusPositionStreamProducer {
	Logger logger = LoggerFactory.getLogger(BusPositionStreamProducer.class);
	
	@Autowired
	private KafkaTemplate<String, BusPosition> kafkaTemplate;
	
	@Value("${avro.topic.name}")
	private String topicName;
	
	public void send(BusPosition position){
		CompletableFuture<SendResult<String, BusPosition>> future = kafkaTemplate.send(
						topicName,
						(String) position.getId(),
						position
		);
		future.thenAccept(sendResult -> {
			logger.info("Message produced successfully {}", sendResult);
		}).exceptionally(exception -> {
			logger.error("Error sending message {}", exception.getMessage());
			return null;
		});
	}
}
