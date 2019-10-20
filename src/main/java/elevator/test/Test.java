package elevator.test;


import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import elevator.model.strategytypes.ElevatorSelectionStrategy;
import elevator.model.strategytypes.EmbarkationStrategy;
import elevator.model.strategytypes.FloorQueueMergeStrategy;
import elevator.simulation.BatchRunner;
import elevator.simulation.SimulationParameters;
import elevator.simulation.SimulationParametersPermutator;
import elevator.strategies.elevator.RandomElevatorSelectionStrategy;
import elevator.strategies.embarkation.SimpleEmbarkationStrategy;
import elevator.strategies.queue.ByOrderFloorQueueMergeStrategy;

public class Test {

	//private static Logger logger = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) throws InterruptedException, TimeoutException {

		testWithStrategies(
			new ByOrderFloorQueueMergeStrategy(),
			new RandomElevatorSelectionStrategy(),
			new SimpleEmbarkationStrategy());
	}
	
	public static void testWithStrategies(
			FloorQueueMergeStrategy queueStrategy,
			ElevatorSelectionStrategy elevatorStrategy, 
			EmbarkationStrategy embarkationStrategy) throws InterruptedException, TimeoutException {
		
		SimulationParameters params = new SimulationParameters()
				.withNumberOfPassengers(50)
				.withDelayBetweenPassengers(rand -> 
					Duration.ofSeconds(uniformDistribution(rand, 5, 10)))
				.withNumberOfElevators(4)
				.withNumberOfFloors(10)
				.withSeed(1977)
				.withDebugOutput(false)
				.withElevatorSelectionStrategy(elevatorStrategy)
				.withEmbarkationStrategy(embarkationStrategy)
				.withFloorQueueMergeStrategy(queueStrategy)
				.withTimeFactor(50)
				.withTimeout(Duration.ofMinutes(10));
			
		List<SimulationParameters> paramList = new SimulationParametersPermutator(params)
			.randomSeeds(1977, 1978, 12, 24, 12312314, 582349123, 231355229)
			.permute();
		
		BatchRunner.run(paramList);
	}
	
	private static int uniformDistribution(Random random, int start, int end) {
		return start + random.nextInt(end - start);
	}

}
