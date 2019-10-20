package elevator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Elevator.
 * 
 * Contains a thread responsible for updating state
 */
public class Elevator {

	public enum State {
		GOING_UP,
		GOING_DOWN,
		STOPPED
	}
	
	private volatile int currentFloor;
	private volatile State state;
	
	private List<Passenger> passengers = new ArrayList<>();
	private AtomicBoolean run = new AtomicBoolean(true);
	private Thread controllerThread;
	private Environment env;
	private List<Floor> floors;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String name;
	
	private List<Integer> floorQueue = new ArrayList<>();
	private Object floorQueueMonitor = new Object();
	private volatile int destinationFloor;
	
	
	public Elevator(Environment env, List<Floor> floors, int name) {
		this.env = env;
		this.floors = floors;
		this.name = String.valueOf(name);
		state = State.STOPPED;
		currentFloor = 0;
		
		controllerThread = new Thread(this::controllerLoop, "Elevator " + name);
		controllerThread.start();
	}
	
	public String getName() {
		return name;
	}
	
	public State getState() {
		return state;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}

	public void stop() {
		run.set(false);
		controllerThread.interrupt();
		try {
			controllerThread.join(1000);
		} catch (InterruptedException e) {
		}
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	public int getNumberOfPassengers() {
		return passengers.size();
	}

	/**
	 * Main loop of controller thread
	 */
	private void controllerLoop() {
		env.debug(logger, "Controller loop starting");
		while (run.get()) {
			try {
				move(getNextFloor());
			}
			catch (InterruptedException e) {
				break;
			}
		}
		
		env.debug(logger, "Controller loop exiting");
	}

	/**
	 * Moves to the specified floor 
	 */
	private void move(int floor) throws InterruptedException {
		env.debug(logger, "Going to floor {}", floor);
		destinationFloor = floor;
		
		if (floor == currentFloor) {
			arrived();
		} else {
			state = floor > currentFloor ? State.GOING_UP : State.GOING_DOWN;
			while (floor != currentFloor) {
				env.getClock().sleep(env.getConstants().getTravelTimeBetweenFloors());
				currentFloor += (floor > currentFloor ? 1 : -1);
			}
			arrived();
		}
		destinationFloor = -1;
	}

	/**
	 * Returns the next floor to go to. Blocks if there currently are no floors to go to. 
	 */
	private Integer getNextFloor() throws InterruptedException {
		synchronized (floorQueueMonitor) {

			if (floorQueue.isEmpty()) {
				floorQueueMonitor.wait();
			}
			
			return floorQueue.remove(0);
		}
	}
	
	/**
	 * Called when this elevator has been assigned a passenger. Updates the floor queue. 
	 */
	public void addPassengerFromFloor(Passenger passenger, int fromFloor) {
		synchronized (floorQueueMonitor) {
			env.debug(logger, "Queing floors {} and {} to current queue {}", fromFloor, passenger.getDestinationFloor(), floorQueue);
			
			floorQueue = env.getFloorQueueMergeStrategy().merge(state, currentFloor, floorQueue, fromFloor, passenger.getDestinationFloor());

			env.debug(logger, "Merged queue: {}", floorQueue);
			floorQueueMonitor.notifyAll();
		}
	}

	/**
	 * Performs the arrival procedure, opening doors, letting people off and in, et.c.
	 */
	private void arrived() throws InterruptedException {
		state = State.STOPPED;
		
		env.debug(logger, "Arrived at floor {}. Opening doors..", currentFloor);
		
		// Open doors
		env.getClock().sleep(env.getConstants().getDoorOpenCloseTime());
		
		if (passengers.size() > 0) {
			env.debug(logger, "Letting off passengers");
			
			int passengersLetOff = 0;
			
			// Let off passengers
			Iterator<Passenger> it = passengers.iterator();
			while (it.hasNext()) {
				Passenger passenger = it.next();
				if (passenger.getDestinationFloor() == currentFloor) {
					++passengersLetOff;
					it.remove();
					passenger.arrived(env);
					env.getClock().sleep(env.getConstants().getPassengerDisembarkTime());
				}
			}
			
			env.debug(logger, "Let off {} passengers, now taking on passengers...", passengersLetOff);
		}
		
		int passengersTakenOn = 0;
		// Take on passengers
		List<Passenger> pickUpPassengers = floors.get(currentFloor).pickUpPassengers(this);
		for (Passenger passenger : pickUpPassengers) {
			env.getClock().sleep(env.getConstants().getPassengerDisembarkTime());
			++passengersTakenOn;
			passengers.add(passenger);
		}
		
		// Close doors
		env.debug(logger, "Took on {} passengers, now closing doors...", passengersTakenOn);
		
		env.getClock().sleep(env.getConstants().getDoorOpenCloseTime());
		
		env.debug(logger, "Arrival procedure complete");
	}

	public boolean isGoingToFloor(int floor) {
		return floorQueue.contains(floor);
	}
	
	@Override
	public String toString() {
		String floorQueueStr;
		synchronized (floorQueueMonitor) {
			floorQueueStr = floorQueue.toString();
		}
		return String.format("[Elevator %s. Num passengers = %d. Current floor = %d, State = %s, Floor queue: %s]", 
			name, passengers.size(), currentFloor, state, floorQueueStr); 
	}

	/**
	 * Get's the current state if going up or down
	 * 
	 * If stopped, check if we have a next floor queued and if so if we need to go up or down
	 */
	public State getStateOrProjectedState() {
		if (state != State.STOPPED) {
			return state;
		}
		int nextFloor = -1;
		synchronized (floorQueueMonitor) {
			if (floorQueue.size() > 0) {
				nextFloor = floorQueue.get(0);
			}
		}
		if (nextFloor == -1) {
			return state;
		}
		return nextFloor > currentFloor ? State.GOING_UP : State.GOING_DOWN;
	}
}
