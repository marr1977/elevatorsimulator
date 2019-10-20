package elevator.model;

import java.time.Duration;

public class Clock {

	private double factor;

	// factor = 1 => real time
	// factor < 1 => slower
	// factor > 1 => faster
	public Clock(double factor) {
		if (Double.compare(0d, factor) == 0) {
			throw new IllegalArgumentException("Factor can't be zero");
		}
		this.factor = factor;
	}
	
	public void sleep(Duration dur) throws InterruptedException {
		Thread.sleep(getRealTime(dur).toMillis());
	}

	private Duration getRealTime(Duration dur) {
		return Duration.ofMillis((long) ((double) dur.toMillis() / factor));
	}
	
	public Duration getSimulatedTime(Duration dur) {
		return Duration.ofMillis((long) ((double) dur.toMillis() * factor));
	}
	
}
