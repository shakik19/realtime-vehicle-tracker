package bd.shakik.realtime.tracking.Controller;

import bd.shakik.avro.schemas.BusPosition;
import bd.shakik.realtime.tracking.Producer.BusPositionStreamProducer;
import bd.shakik.realtime.tracking.Service.DataPoller;
import bd.shakik.realtime.tracking.Service.VehiclePositionDataProcessor;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@EnableScheduling
public final class PipelineController {
	Logger logger = LoggerFactory.getLogger(PipelineController.class);
	
	private final DataPoller dataPoller;
	private final VehiclePositionDataProcessor dataProcessor;
	private final BusPositionStreamProducer busPositionStreamProducer;
	private int count = 1;
	
	public PipelineController(
					DataPoller dataPoller,
					VehiclePositionDataProcessor dataProcessor,
					BusPositionStreamProducer busPositionStreamProducer) {
		this.dataPoller = dataPoller;
		this.dataProcessor = dataProcessor;
		this.busPositionStreamProducer = busPositionStreamProducer;
	}
	
	@Scheduled(cron = "*/120 * * * * *")
	private void produceBusPositionStream() {
		try {
			FeedMessage feed = dataPoller.getBusFeed();
			for (FeedEntity entity : feed.getEntityList()) {
				BusPosition busPosition = dataProcessor.getBusPosition(entity);
				if (busPosition != null) {
					busPositionStreamProducer.send(busPosition);
					logger.info("{}. Kafka Topic: {} | Added a new message Key: {}",
									count++,
									busPositionStreamProducer.getTopicName(),
									busPosition.getId());
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Got exception at {}", PipelineController.class);
			throw new RuntimeException(e);
		}
	}
}
	
