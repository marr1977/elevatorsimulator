package elevator.model;

import java.util.ArrayList;
import java.util.List;

public class ElevatorSystem {

	private List<Elevator> elevators;
	private List<Floor> floors;
	
	public ElevatorSystem(int numElevators, int numFloors, Environment env) {

		floors = new ArrayList<>(numFloors);
		for (int i = 0; i < numFloors; ++i) {
			floors.add(new Floor(env, i));
		}

		elevators = new ArrayList<>(numElevators);
		for (int i = 0; i < numElevators; ++i) {
			elevators.add(new Elevator(env, floors, i));
		}
		
		env.setElevators(elevators);
		env.setFloors(floors);
	}

	public void stop() {
		elevators.forEach(e -> e.stop());
	}

	public void submitPassenger(Passenger passenger, int floor) {
		floors.get(floor).addPassenger(passenger);
	}
	
	
}
