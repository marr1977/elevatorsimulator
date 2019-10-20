package elevator.model.strategytypes;

import java.util.List;

import elevator.model.Elevator.State;

/**
 * Called when an elevator wants to schedule a new passenger to get a new floor stop list
 */
public interface FloorQueueMergeStrategy {

	List<Integer> merge(State state, int currentFloor, List<Integer> currentQueue, int fromFloor, int toFloor);
}
