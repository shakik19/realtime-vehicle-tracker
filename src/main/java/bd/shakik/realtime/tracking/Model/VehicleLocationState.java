package bd.shakik.realtime.tracking.Model;

import bd.shakik.avro.schemas.Location;

public record VehicleLocationState(Location location, long timestamp) {
}
