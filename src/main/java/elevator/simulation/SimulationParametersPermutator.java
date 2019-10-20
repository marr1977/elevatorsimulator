package elevator.simulation;

import java.util.ArrayList;
import java.util.List;

public class SimulationParametersPermutator {

	private long[] randomSeeds;
	private int[] numElevators;
	private int[] numFloors;
	private SimulationParameters baseConfig;

	public SimulationParametersPermutator(SimulationParameters baseConfig) {
		this.baseConfig = baseConfig;
	}

	public SimulationParametersPermutator randomSeeds(long... randomSeeds) {
		this.randomSeeds = randomSeeds;
		return this;
	}
	
	public SimulationParametersPermutator numElevators(int... numElevators) {
		this.numElevators = numElevators;
		return this;
	}
	
	
	public SimulationParametersPermutator numFloors(int... numFloors) {
		this.numFloors = numFloors;
		return this;
	}

	public List<SimulationParameters> permute() {
		
		List<SimulationParameters> list = new ArrayList<>();
		
		if (randomSeeds == null) {
			randomSeeds = new long[] { baseConfig.randomSeed };
		}
		
		if (numElevators == null) {
			numElevators = new int[] { baseConfig.numElevators };
		}

		if (numFloors == null) {
			numFloors = new int[] { baseConfig.numFloors };
		}
		
		for (long randomSeed : randomSeeds) {
			for (int numElevator : numElevators) {
				for (int numFloor : numFloors) {
					list.add(new SimulationParameters(baseConfig)
						.withNumberOfFloors(numFloor)
						.withNumberOfElevators(numElevator)
						.withSeed(randomSeed));
				}
			}
		}
		

		return list;
	}
}
