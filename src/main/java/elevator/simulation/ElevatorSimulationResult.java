package elevator.simulation;

import java.time.Duration;

public class ElevatorSimulationResult {

	Duration realTimeDuration;
	Duration simTimeDuration;

	public ElevatorSimulationResult(Duration realTimeDuration, Duration simTimeDuration) {
		this.realTimeDuration = realTimeDuration;
		this.simTimeDuration = simTimeDuration;
	}
	
	@Override
	public String toString() {
		return String.format("Simulation time duration = %d seconds. Real time duration = %d seconds", simTimeDuration.toSeconds(), realTimeDuration.toSeconds());
	}
}
