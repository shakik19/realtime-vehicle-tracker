package bd.shakik.realtime.tracking.Service;

public class Calculator {
	final static int R = 6371; // Radius of the earth in kilometers
	
	public static Double calculateDistance(Double prevLat, Double prevLon, Double currLat, Double currLon) {
		Double latDistance = toRad(currLat - prevLat);
		Double lonDistance = toRad(currLon - prevLon);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
						Math.cos(toRad(prevLat)) * Math.cos(toRad(currLat)) *
										Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		Double distance = R * c;
		
		return distance; // in kilometers
	}
	
	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}
	
	public static double calculateKmPerHour(double meters, long seconds) {
		if (seconds == 0) {
			return 0;
		} else {
			double metersPerSecond = meters / seconds;
			return metersPerSecond * 3.6;
		}
	}
}