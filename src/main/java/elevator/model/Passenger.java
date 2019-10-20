package elevator.model;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Passenger {

	private static Logger logger = LoggerFactory.getLogger(Passenger.class);
	
	private int destinationFloor;
	private String name;
	private boolean arrived;
	private long departureTime = System.currentTimeMillis();

	private Duration simDuration;
	
	public Passenger(String name, int destinationFloor) {
		this.name = name;
		this.destinationFloor = destinationFloor;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public String getName() {
		return name;
	}

	public void arrived(Environment env) {
		long arrivalTime = System.currentTimeMillis();
		long realDuration = arrivalTime - departureTime;
		simDuration = env.getClock().getSimulatedTime(Duration.ofMillis(realDuration)); 
		
		env.debug(logger, "Passenger {} arrived after {}", name, simDuration);
		arrived = true;
	}
	
	public boolean hasArrived() {
		return arrived;
	}
	
	public Duration getTravelDuration() {
		return simDuration;
	}
	
	@Override
	public String toString() {
		return String.format("%s => %d", name, destinationFloor);
	}
}
