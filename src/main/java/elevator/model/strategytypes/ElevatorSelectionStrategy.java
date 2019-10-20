package elevator.model.strategytypes;

import elevator.model.Elevator;
import elevator.model.Environment;
import elevator.model.Floor;
import elevator.model.Passenger;

/**
 * Called upon when a new passenger arrives and requests an elevatgor.
 *  
 * Strategy should return the elevator that should handle this passenger. 
 */
public interface ElevatorSelectionStrategy {
	Elevator getElevatorFor(Passenger passenger, Floor departureFloor, Environment env);
}
