package elevator.strategies.embarkation;

import elevator.model.Elevator;
import elevator.model.Floor;
import elevator.model.Passenger;

/**
 * This strategy embarks passengers on elevators that are going to their destination floor 
 */
public class SimpleEmbarkationStrategy implements elevator.model.strategytypes.EmbarkationStrategy {

	@Override
	public boolean shouldPassengerEmbark(Passenger passenger, Floor fromFloor, Elevator onElevator) {
		
		if (onElevator.isGoingToFloor(passenger.getDestinationFloor())) {
			return true;
		}
		
		return false;
	}

}
