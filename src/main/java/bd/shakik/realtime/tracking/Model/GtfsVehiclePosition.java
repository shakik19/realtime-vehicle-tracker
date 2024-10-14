package bd.shakik.realtime.tracking.Model;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.*;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition.*;

public final class GtfsVehiclePosition {
	private final String tripId;
	private final ScheduleRelationship scheduleRelationship;
	private final String routeId;
	private final int directionId;
	private final VehicleStopStatus currentStatus;
	private final long timestamp;
	private final String stopId;
	private final String vehicleId;
	private final String vehicleLabel;
	private final double latitude;
	private final double longitude;
	private final float bearing;
	private final OccupancyStatus occupancyStatus;
	
	public GtfsVehiclePosition(GtfsRealtime.VehiclePosition vehiclePosition) {
		this.tripId = vehiclePosition.getTrip().getTripId();
		this.scheduleRelationship = vehiclePosition.getTrip().getScheduleRelationship();
		this.routeId = vehiclePosition.getTrip().getRouteId();
		this.directionId = vehiclePosition.getTrip().getDirectionId();
		this.currentStatus = vehiclePosition.getCurrentStatus();
		this.timestamp = vehiclePosition.getTimestamp();
		this.stopId = vehiclePosition.getStopId();
		this.vehicleId = vehiclePosition.getVehicle().getId();
		this.vehicleLabel = vehiclePosition.getVehicle().getLabel();
		this.latitude = vehiclePosition.getPosition().getLatitude();
		this.longitude = vehiclePosition.getPosition().getLongitude();
		this.bearing = vehiclePosition.getPosition().getBearing();
		this.occupancyStatus = vehiclePosition.getOccupancyStatus();
	}
	
	public String getTripId() {
		return tripId;
	}
	
	public ScheduleRelationship getScheduleRelationship() {
		return scheduleRelationship;
	}
	
	public String getRouteId() {
		return routeId;
	}
	
	public int getDirectionId() {
		return directionId;
	}
	
	public VehicleStopStatus getCurrentStatus() {
		return currentStatus;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getStopId() {
		return stopId;
	}
	
	public String getVehicleId() {
		return vehicleId;
	}
	
	public String getVehicleLabel() {
		return vehicleLabel;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getBearing() {
		return bearing;
	}
	
	public OccupancyStatus getOccupancyStatus() {
		return occupancyStatus;
	}
	
	@Override
	public String toString() {
		return "GtfsVehiclePosition{" +
						"vehicleId='" + vehicleId + '\'' +
						", vehicleLabel='" + vehicleLabel + '\'' +
						", latitude=" + latitude +
						", longitude=" + longitude +
						", bearing=" + bearing +
						", tripId='" + tripId + '\'' +
						", scheduleRelationship=" + scheduleRelationship +
						", routeId='" + routeId + '\'' +
						", directionId=" + directionId +
						", currentStatus=" + currentStatus +
						", timestamp=" + timestamp +
						", stopId='" + stopId + '\'' +
						", occupancyStatus=" + occupancyStatus +
						'}';
	}
}
