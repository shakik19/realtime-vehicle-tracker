package bd.shakik.realtime.tracking.Controller;

import bd.shakik.avro.schemas.BusPosition;
import bd.shakik.realtime.tracking.Service.DataPoller;
import bd.shakik.realtime.tracking.Service.MessageProducer;
import bd.shakik.realtime.tracking.Service.BusPositionDataProcessor;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@EnableScheduling
public final class PipelineController {
	Logger logger = LoggerFactory.getLogger(PipelineController.class);
	
	private final DataPoller dataPoller;
	private final BusPositionDataProcessor dataProcessor;
	private final MessageProducer busPositionMessageProducer;
	@Value("${kafka.topics.bus-position}")
	private String busPositionStreamTopicName;
	private int count = 1;
	
	public PipelineController(
					DataPoller dataPoller,
					BusPositionDataProcessor dataProcessor,
					MessageProducer busPositionMessageProducer) {
		this.dataPoller = dataPoller;
		this.dataProcessor = dataProcessor;
		this.busPositionMessageProducer = busPositionMessageProducer;
	}
	
	@Scheduled(cron = "*/120 * * * * *")
	private void streamBusPosition() {
		final String url = "https://www.rtd-denver.com/files/gtfs-rt/VehiclePosition.pb";;
		try {
			FeedMessage feed = dataPoller.getBusFeed(url);
			for (FeedEntity entity : feed.getEntityList()) {
				BusPosition busPosition = dataProcessor.getBusPosition(entity);
				if (busPosition != null) {
					busPositionMessageProducer.sendBusPosition(busPosition);
					logger.info("{}. Kafka Topic: {} | Added a new message Key: {}",
									count++,
									busPositionStreamTopicName,
									busPosition.getId());
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Got exception at {}", PipelineController.class);
			throw new RuntimeException(e);
		}
	}
}
	
