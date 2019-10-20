package elevator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Floor {

	private List<Passenger> waitingPassengers = new LinkedList<>();
	private Environment env;
	private int floor;
	
	public Floor(Environment env, int floor) {
		this.env = env;
		this.floor = floor;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void addPassenger(Passenger passenger) {
		synchronized (waitingPassengers) {
			waitingPassengers.add(passenger);
		}
		
		Elevator elevator = env.getElevatorSelectionStrategy().getElevatorFor(passenger, this, env);
		elevator.addPassengerFromFloor(passenger, floor);
	}
	
	public List<Passenger> getPassengers() {
		synchronized (waitingPassengers) {
			return new ArrayList<>(waitingPassengers);
		}
	}
	
	/**
	 * Returns a list of passengers that should embark on this elevator 
	 */
	public List<Passenger> pickUpPassengers(Elevator elevator) {
		List<Passenger> embarking = new ArrayList<>();
		synchronized (waitingPassengers) {
			Iterator<Passenger> it = waitingPassengers.iterator();
			while (it.hasNext()) {
				Passenger passenger = it.next();
				if (env.getEmbarkationStrategy().shouldPassengerEmbark(passenger, this, elevator)) {
					it.remove();
					embarking.add(passenger);
				}
			}
		}
		return embarking;
	}
	
	@Override
	public String toString() {
		return String.format("[Floor %d. Passengers waiting: %s]", floor, waitingPassengers);
	}

	public String getName() {
		return String.valueOf(floor);
	}
}
