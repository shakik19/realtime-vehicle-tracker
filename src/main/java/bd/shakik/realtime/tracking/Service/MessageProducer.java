package bd.shakik.realtime.tracking.Service;

import bd.shakik.avro.schemas.BusPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageProducer {
	Logger logger = LoggerFactory.getLogger(MessageProducer.class);
	
	@Autowired
	@Qualifier("busPositionKafkaTemplate")
	private KafkaTemplate<String, BusPosition> busPositionKafkaTemplate;
	
	@Value("${kafka.topics.bus-position}")
	private String bustPositionTopicName;
	
	public void sendBusPosition(BusPosition position) {
		Message<BusPosition> message = MessageBuilder
						.withPayload(position)
						.setHeader(KafkaHeaders.TOPIC, bustPositionTopicName)
						.setHeader(KafkaHeaders.KEY, position.getId())
						.setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
						.build();
		CompletableFuture<SendResult<String, BusPosition>> future = busPositionKafkaTemplate.send(message);
		future.thenAccept(sendResult -> {
			logger.info("Message produced successfully | {}", sendResult.getProducerRecord());
		}).exceptionally(exception -> {
			logger.error("Error producing message | {}", exception.getMessage());
			return null;
		});
	}
}
