package bd.shakik.realtime.tracking.Producer;

import bd.shakik.avro.schemas.BusPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BusPositionStreamProducer {
	Logger logger = LoggerFactory.getLogger(BusPositionStreamProducer.class);
	
	@Autowired
	private KafkaTemplate<String, BusPosition> kafkaTemplate;
	
	@Value("${kafka.topic.name}")
	private String topicName;
	
	public void send(BusPosition position){
		Message<BusPosition> message = MessageBuilder
						.withPayload(position)
						.setHeader(KafkaHeaders.TOPIC, topicName)
						.setHeader(KafkaHeaders.KEY, position.getId())
						.setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
						.build();
		CompletableFuture<SendResult<String, BusPosition>> future = kafkaTemplate.send(message);
		future.thenAccept(sendResult -> {
			logger.info("Message produced successfully | {}", sendResult.getProducerRecord());
		}).exceptionally(exception -> {
			logger.error("Error producing message | {}", exception.getMessage());
			return null;
		});
	}
	
	public String getTopicName() {
		return topicName;
	}
}
