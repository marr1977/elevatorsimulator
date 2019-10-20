package elevator.strategies.elevator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elevator.model.Elevator;
import elevator.model.Elevator.State;
import elevator.model.Environment;
import elevator.model.Floor;
import elevator.model.Passenger;

/**
 * Finds the nearest elevator heading towards the floor 
 */
public class NearestHeadingTowardsElevatorStrategy implements elevator.model.strategytypes.ElevatorSelectionStrategy {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private RandomElevatorSelectionStrategy random = new RandomElevatorSelectionStrategy();
	
	@Override
	public Elevator getElevatorFor(Passenger passenger, Floor departureFloor, Environment env) {
		
		Elevator closest = null;
		int closestFloorsAway = Integer.MAX_VALUE;
		
		StringBuilder debugOutput = new StringBuilder();
		debugOutput.append("Nearest heading towards floor ").append(departureFloor.getFloor()).append("\n");
		
		for (Elevator elevator : env.getElevators()) {
			//
			// Note that a particular elevator's state can change while we are examining it. We just have to deal with it and accept possible bad behavior
			//
			Elevator.State state = elevator.getStateOrProjectedState();
			
			if (state == State.GOING_UP) {
				if (elevator.getCurrentFloor() > departureFloor.getFloor()) {
					debugOutput.append(elevator).append(": Not applicable, going up and is above our floor\n");
					// Not applicable
					continue;
				}
			} else if (state == State.GOING_DOWN) {
				if (elevator.getCurrentFloor() < departureFloor.getFloor()) {
					debugOutput.append(elevator).append(": Not applicable, going down and is below our floor\n");
					// Not applicable
					continue;
				}
			} else {
				// Standing still, that's ok
			}
			
			int floorsAway = Math.abs(departureFloor.getFloor() - elevator.getCurrentFloor());
			debugOutput.append(elevator).append(": ").append(floorsAway).append(" floors away\n");
			
			if (closestFloorsAway > floorsAway) {
				closestFloorsAway = floorsAway;
				closest = elevator;
			}
		}
		debugOutput.append("Closest: ").append(closest);
		
		env.debug(logger, debugOutput.toString());
		
		if (closest != null) {
			env.debug(logger, "Closest heading towards floor {}: {}", departureFloor.getFloor(), closest);
			return closest;
		}
		
		return random.getElevatorFor(passenger, departureFloor, env);
	}
}