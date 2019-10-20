package elevator.model;

import java.time.Duration;

public class Constants {
	private final Duration travelTimeBetweenFloors = Duration.ofSeconds(2);
	private final Duration passengerDisembarkTime = Duration.ofSeconds(1);
	private final Duration doorOpenCloseTime = Duration.ofSeconds(2);
	
	public Duration getTravelTimeBetweenFloors() {
		return travelTimeBetweenFloors;
	}

	public Duration getPassengerDisembarkTime() {
		return passengerDisembarkTime;
	}
	
	public Duration getDoorOpenCloseTime() {
		return doorOpenCloseTime;
	}
}
