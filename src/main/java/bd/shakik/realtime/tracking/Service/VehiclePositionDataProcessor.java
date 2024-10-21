package bd.shakik.realtime.tracking.Service;

import bd.shakik.avro.schemas.BusPosition;
import bd.shakik.avro.schemas.Location;
import bd.shakik.realtime.tracking.Model.GtfsVehiclePosition;
import bd.shakik.realtime.tracking.Model.VehicleLocationState;
import com.google.transit.realtime.GtfsRealtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class VehiclePositionDataProcessor {
	Logger logger = LoggerFactory.getLogger(VehiclePositionDataProcessor.class);
	
	private static final HashMap<String, VehicleLocationState> locationStateStore = new HashMap<>();
	
	public BusPosition getBusPosition(FeedEntity entity) {
		if (!entity.hasVehicle()) {
			logger.error("Vehicle Position Object not found");
			return null;
		}else{
			logger.debug("{} Exists in FeedEntity", VehicleDescriptor.class);
		}
		GtfsVehiclePosition gtfsVehiclePosition = new GtfsVehiclePosition(entity.getVehicle());
		logger.debug("Parsed FeedEntity id: {} to {}",gtfsVehiclePosition.getVehicleId(), GtfsVehiclePosition.class);
		logger.debug("GtfVehiclePosition Object: {}", gtfsVehiclePosition);
		
		Location busLocation = new Location(gtfsVehiclePosition.getLongitude(), gtfsVehiclePosition.getLatitude());
		
		logger.debug("Building BusPosition object of vehicle id: {}", gtfsVehiclePosition.getVehicleId());
		return new BusPosition(
						gtfsVehiclePosition.getVehicleId(),
						gtfsVehiclePosition.getTimestamp(),
						busLocation,
						gtfsVehiclePosition.getBearing(),
						getSpeed(gtfsVehiclePosition)
		);
	}
	
	private double getSpeed(GtfsVehiclePosition currentPosition) {
		final String vehicleId = currentPosition.getVehicleId();
		logger.debug("Getting speed for id: {}", vehicleId);
		
		Location currLoc = new Location(currentPosition.getLongitude(), currentPosition.getLatitude());
		VehicleLocationState currLocState = new VehicleLocationState(currLoc, currentPosition.getTimestamp());
		logger.debug("Current Location State: {}", currLocState);
		
		//! Checking if the current vehicle has an entry
		if (locationStateStore.containsKey(vehicleId)) {
			logger.debug("Previous Location State: {}", locationStateStore.get(vehicleId));
			Location prevLoc = locationStateStore.get(vehicleId).location();
			double distanceKm = Calculator.calculateDistance(
							prevLoc.getLat(),
							prevLoc.getLon(),
							currLoc.getLat(),
							currLoc.getLon()
			);
			long timeDelta = currLocState.timestamp() - locationStateStore.get(vehicleId).timestamp();
			
			locationStateStore.put(vehicleId, currLocState);
			
			return Calculator.calculateKmPerHour(distanceKm * 1000, timeDelta);
		} else {
			logger.info("Vehicle Id: {} | Doesn't exists in the State Store", vehicleId);
			locationStateStore.put(vehicleId, currLocState);
			logger.debug("Putted in the State Store: {}", locationStateStore.get(vehicleId));
			return 0d;
		}
	}
}
