package elevator.simulation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchRunner {
	private static Logger logger = LoggerFactory.getLogger(BatchRunner.class);
	
	public static void run(List<SimulationParameters> paramList) throws InterruptedException, TimeoutException {
		List<ElevatorSimulationResult> results = new ArrayList<>();
		
		for (SimulationParameters params : paramList) {
			ElevatorSimulator simulator = new ElevatorSimulator(params);			
			
			ElevatorSimulationResult result = simulator.run();
			results.add(result);
		}
		
		Duration sum = Duration.ZERO;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paramList.size(); ++i) {
			sb.append("\nParams: ").append(paramList.get(i).toString())
			.append("\nResult: ").append(results.get(i).toString()).append("\n");
			
			sum = sum.plus(results.get(i).simTimeDuration);
		}
		
		sb.append("\nAverage: ").append(sum.toSeconds() / paramList.size()).append(" seconds");
		
		logger.info(sb.toString());
		
	}
}
