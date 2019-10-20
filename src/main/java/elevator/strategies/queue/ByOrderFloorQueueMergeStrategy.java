package elevator.strategies.queue;

import java.util.List;

import elevator.model.Elevator.State;
import elevator.model.strategytypes.FloorQueueMergeStrategy;

/**
 * Goes to each floor in order that they were requested 
 */
public class ByOrderFloorQueueMergeStrategy implements FloorQueueMergeStrategy {

	@Override
	public List<Integer> merge(
			State state, 
			int currentFloor, 
			List<Integer> currentQueue, 
			int fromFloor,
			int destFloor) {
		
		int fromIdx = currentQueue.indexOf(fromFloor);
		int destIdx = currentQueue.indexOf(destFloor);

		// Both floors exist in the current queue 
		if (fromIdx != -1 && destIdx != -1) {
			if (destIdx < fromIdx) {
				// They both exist, but in the reverse order, add the destination floor
				currentQueue.add(destFloor);	
			}
		}
		else {
			if (fromIdx == -1) {
				currentQueue.add(fromFloor);
				// We must add the to floor, regardless if it exists or not since it must come after the "from floor"
				currentQueue.add(destFloor); 
			} else {
				// From exists but destination doesn't, add it
				currentQueue.add(destFloor); 
			}
		}
		
		return currentQueue;
	}

}
