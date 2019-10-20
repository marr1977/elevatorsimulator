package elevator.model.strategytypes;

import elevator.model.Elevator;
import elevator.model.Floor;
import elevator.model.Passenger;

/**
 * Called when an elevator arrives at a floor, returns true if the passenger
 * should embark on the elevator and false if not 
 */
public interface EmbarkationStrategy {
	boolean shouldPassengerEmbark(Passenger passenger, Floor fromFloor, Elevator onElevator);
}
