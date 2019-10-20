package elevator.strategies.elevator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elevator.model.Elevator;
import elevator.model.Environment;
import elevator.model.Floor;
import elevator.model.Passenger;

/**
 * A random elevator is assigned 
 */
public class RandomElevatorSelectionStrategy implements elevator.model.strategytypes.ElevatorSelectionStrategy {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Elevator getElevatorFor(Passenger passenger, Floor departureFloor, Environment env) {
		Elevator elevator = env.getElevators().get(env.getRandom().nextInt(env.getElevators().size()));
		
		env.debug(logger, "Randomly picked elevator {} for new passenger", elevator);
		return elevator;
	}

}
