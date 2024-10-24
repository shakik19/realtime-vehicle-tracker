package bd.shakik.realtime.tracking.Service;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kong.unirest.core.Unirest;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DataPoller {
	private static final Logger logger = LoggerFactory.getLogger(DataPoller.class);
	
	public FeedMessage getBusFeed() throws ExecutionException, InterruptedException {
		final String url = "https://www.rtd-denver.com/files/gtfs-rt/VehiclePosition.pb";
		CompletableFuture<FeedMessage> future = new CompletableFuture<>();
		logger.info("Requesting GTFS-RT protobuf from {}", url);
		Unirest.get(url).thenConsume(rawResponse -> {
			try {
				logger.info("Received the protobuf packet");
				
				final InputStream inputStream = rawResponse.getContent();
				logger.debug("Parsed to {}", InputStream.class);
				
				final FeedMessage feed = FeedMessage.parseFrom(inputStream);
				logger.debug("Parsed to {}", FeedMessage.class);
				
				logger.info("Total entities: {}", feed.getEntityCount());
				future.complete(feed);
			} catch (Exception e) {
				logger.error("Error fetching or Parsing the Protobuf into {}: {}", FeedMessage.class, e.getMessage());
				future.completeExceptionally(e);
			}
		});
		return future.get();
	}
}

