package elevator.simulation;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;

import elevator.model.strategytypes.ElevatorSelectionStrategy;
import elevator.model.strategytypes.EmbarkationStrategy;
import elevator.model.strategytypes.FloorQueueMergeStrategy;

public class SimulationParameters {
	long randomSeed = System.currentTimeMillis();
	int numPassengers;
	double timeFactor;
	int numFloors;
	int numElevators;
	Function<Random, Duration> delayBetweenPassengers;
	Duration timeout = Duration.ofMinutes(10);
	EmbarkationStrategy embarkationStrategy;
	ElevatorSelectionStrategy elevatorSelectionStrategy;
	FloorQueueMergeStrategy floorQueueMergeStrategy;
	boolean debugOutput;
	
	public SimulationParameters() {
	}
	
	public SimulationParameters(SimulationParameters copy) {
		this.randomSeed = copy.randomSeed;
		this.numPassengers = copy.numPassengers;
		this.timeFactor = copy.timeFactor;
		this.numFloors = copy.numFloors;
		this.numElevators = copy.numElevators;
		this.delayBetweenPassengers = copy.delayBetweenPassengers;
		this.timeout = copy.timeout;
		this.embarkationStrategy = copy.embarkationStrategy;
		this.elevatorSelectionStrategy = copy.elevatorSelectionStrategy;
		this.floorQueueMergeStrategy = copy.floorQueueMergeStrategy;
		this.debugOutput = copy.debugOutput;
	}
	
	@Override
	public String toString() {
		return String.format("[Seed = %d, NumPassengers = %d, Floors = %d, Elevators = %d, EmbarkStrat = %s, ElevatorStrat = %s, MergeStrat = %s]",
				randomSeed, numPassengers, numFloors, numElevators, embarkationStrategy.getClass().getSimpleName(), 
				elevatorSelectionStrategy.getClass().getSimpleName(), floorQueueMergeStrategy.getClass().getSimpleName());
	}


	public SimulationParameters withSeed(long randomSeed) {
		this.randomSeed = randomSeed;
		return this;
	}

	public SimulationParameters withDelayBetweenPassengers(Function<Random, Duration> delayBetweenPassengers) {
		this.delayBetweenPassengers = delayBetweenPassengers;
		return this;
	}
	
	public SimulationParameters withNumberOfPassengers(int numPassengers) {
		this.numPassengers = numPassengers;
		return this;
	}

	public SimulationParameters withNumberOfElevators(int numElevators) {
		this.numElevators = numElevators;
		return this;
	}

	public SimulationParameters withNumberOfFloors(int numFloors) {
		this.numFloors = numFloors;
		return this;
	}

	public SimulationParameters withTimeFactor(double timeFactor) {
		this.timeFactor = timeFactor;
		return this;
	}
	
	public SimulationParameters withFloorQueueMergeStrategy(FloorQueueMergeStrategy strategy) {
		floorQueueMergeStrategy = strategy;
		return this;
	}
	
	public SimulationParameters withElevatorSelectionStrategy(ElevatorSelectionStrategy strategy) {
		elevatorSelectionStrategy = strategy;
		return this;
	}

	public SimulationParameters withEmbarkationStrategy(EmbarkationStrategy strategy) {
		embarkationStrategy = strategy;
		return this;
	}

	public SimulationParameters withTimeout(Duration timeout) {
		this.timeout = timeout;
		return this;
	}

	public SimulationParameters withDebugOutput(boolean debugOutput) {
		this.debugOutput = debugOutput;
		return this;
	}
	
	

}
