package elevator.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elevator.model.Clock;
import elevator.model.Constants;
import elevator.model.Elevator;
import elevator.model.ElevatorSystem;
import elevator.model.Environment;
import elevator.model.Floor;
import elevator.model.Passenger;

public class ElevatorSimulator {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private SimulationParameters params;
	private Environment env;
	private ElevatorSystem system;

	private Instant timeout;
	private List<Passenger> passengers;

	private Thread loggerThread;
	
	public ElevatorSimulator(SimulationParameters params) {
		if (params.numFloors < 2) {
			throw new IllegalArgumentException("Invalid number of floors");
		}
		
		if (params.numElevators < 1) {
			throw new IllegalArgumentException("Invalid number of elevators");
		}

		if (params.numPassengers < 1) {
			throw new IllegalArgumentException("Invalid number of passengers");
		}

		Objects.requireNonNull(params.elevatorSelectionStrategy);
		Objects.requireNonNull(params.embarkationStrategy);
		Objects.requireNonNull(params.floorQueueMergeStrategy);
		Objects.requireNonNull(params.delayBetweenPassengers);

		this.params = params;
		
		env = new Environment(
			new Clock(params.timeFactor), 
			new Constants(),
			new Random(params.randomSeed),
			params.debugOutput,
			params.elevatorSelectionStrategy,
			params.embarkationStrategy,
			params.floorQueueMergeStrategy);
	
		system = new ElevatorSystem(params.numElevators, params.numFloors, env);
		
		passengers = new ArrayList<>(params.numPassengers);
		for (int i = 0; i < params.numPassengers; ++i) {
			int destinationFloor = getRandomBetweenAndNot(0, params.numFloors, -1);
			passengers.add(new Passenger(String.format("P%d", i + 1), destinationFloor));
		}
		
		loggerThread = new Thread(this::loggerLoop, "Logger loop");
		loggerThread.setDaemon(true);
		loggerThread.start();
	}

	public Environment getEnvironment() {
		return env;
	}
	
	private void loggerLoop() {
		while (true) {
			
			logger.info(createStatus());
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private String createStatus() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nFloors: \n");
		for (Floor floor : env.getFloors()) {
			sb.append(floor.toString()).append("\n");
		}
		sb.append("\nElevators: \n");
		for (Elevator elevator : env.getElevators()) {
			sb.append(elevator.toString()).append("\n");
		}
		
		return sb.toString();
	}

	public ElevatorSimulationResult run() throws InterruptedException, TimeoutException {

		this.timeout = Instant.now().plus(params.timeout);
		
		long start = System.currentTimeMillis();
		deployPassengers();
		
		waitUntilAllPassengersArrived();
		long end = System.currentTimeMillis();
		
		loggerThread.interrupt();		
		system.stop();

		Duration realTimeDuration = Duration.ofMillis(end - start);
		Duration simTimeDuration = env.getClock().getSimulatedTime(realTimeDuration);
		
		return new ElevatorSimulationResult(realTimeDuration, simTimeDuration);
	}
	
	public void stop() {
		loggerThread.interrupt();
		system.stop();
	}

	private void deployPassengers() throws InterruptedException {
		logger.info("Deploying passengers");
		
		for (Passenger p : passengers) {
			int departureFloor = getRandomBetweenAndNot(0, params.numFloors, p.getDestinationFloor());
			
			env.debug(logger, "Submitting passenger {} to floor {} going to floor {}", p.getName(), 
				departureFloor, p.getDestinationFloor());
			
			system.submitPassenger(p, departureFloor);
			
			env.getClock().sleep(params.delayBetweenPassengers.apply(env.getRandom()));
		}
		
		logger.info("All passengers have been deployed");
	}
	
	private int getRandomBetweenAndNot(int rangeStart, int rangeEnd, int exclude) {
		int result = exclude;
		while (result == exclude) {
			result = rangeStart + env.getRandom().nextInt(rangeEnd - rangeStart);
		}
		return result;
	}
	
	private void waitUntilAllPassengersArrived() throws TimeoutException, InterruptedException {

		while (Instant.now().isBefore(timeout)) {
			if (hasAllPassengersArrived()) {
				break;
			}
			Thread.sleep(10);
		}
		
		if (hasAllPassengersArrived()) {
			logger.info("All passengers have arrived");
		} else {
			throw new TimeoutException("Simulation timed out before all passengers have arrived");
		}
		
	}

	private boolean hasAllPassengersArrived() {
		return passengers.stream().allMatch(Passenger::hasArrived);
	}

	
}
