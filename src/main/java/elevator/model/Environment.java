package elevator.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;

import elevator.model.strategytypes.ElevatorSelectionStrategy;
import elevator.model.strategytypes.EmbarkationStrategy;
import elevator.model.strategytypes.FloorQueueMergeStrategy;

public class Environment {

	private Clock clock;
	private Constants constants;
	private List<Elevator> elevators;
	private List<Floor> floors;
	private Random random;
	private EmbarkationStrategy embarkationStrategy;
	private ElevatorSelectionStrategy elevatorSelectionStrategy;
	private FloorQueueMergeStrategy floorQueueMergeStrategy;
	private boolean debugOutput;
	
	public Environment(
		Clock clock, 
		Constants constants,
		Random random, 
		boolean debugOutput,
		ElevatorSelectionStrategy elevatorSelectionStrategy, 
		EmbarkationStrategy embarkationStrategy,
		FloorQueueMergeStrategy floorQueueMergeStrategy) {
		
		this.clock = clock;
		this.constants = constants;
		this.debugOutput = debugOutput;
		this.elevatorSelectionStrategy = elevatorSelectionStrategy;
		this.embarkationStrategy = embarkationStrategy;
		this.floorQueueMergeStrategy = floorQueueMergeStrategy;
		this.random = random;
	}

	public Random getRandom() {
		return random;
	}
	
	public boolean debugOutput() {
		return debugOutput;
	}

	public ElevatorSelectionStrategy getElevatorSelectionStrategy() {
		return elevatorSelectionStrategy;
	}
	
	public EmbarkationStrategy getEmbarkationStrategy() {
		return embarkationStrategy;
	}
	
	public FloorQueueMergeStrategy getFloorQueueMergeStrategy() {
		return floorQueueMergeStrategy;
	}
	
	public Clock getClock() {
		return clock;
	}
	
	public Constants getConstants() {
		return constants;
	}

	void setElevators(List<Elevator> elevators) {
		this.elevators = Collections.unmodifiableList(elevators);
	}
	
	public List<Elevator> getElevators() {
		return elevators;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = Collections.unmodifiableList(floors);
	}
	
	public List<Floor> getFloors() {
		return floors;
	}

	public void debug(Logger logger, String string, Object... arguments) {
		if (!debugOutput) {
			return;
		}
		logger.info(string, arguments);
	}
}
