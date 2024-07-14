package com.aim.project.uzf.heuristics;

import java.util.Random;

import com.aim.project.uzf.interfaces.HeuristicInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class AdjacentSwap extends HeuristicOperators implements HeuristicInterface {

	public AdjacentSwap(Random random) {

		super(random);
	}

	@Override
	public int apply(UAVSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		// Calculate the internal number of swaps based on IOM in [0,1]
		// Increase swaps by powers of two
		int swapNumber = (int) Math.pow(2, Math.floor(intensityOfMutation/0.2));
		// Gets the solution representation to an array
		int[] solutionRepresentation = solution.getSolutionRepresentation().getSolutionRepresentation();

		// Iterates through the proportional to IOM
		for (int i = 0; i < swapNumber; i++) {
			// Random index to swap
			int index = random.nextInt(solutionRepresentation.length); // excludes the last index
			// Perform swap
			swapAdjacent(solutionRepresentation, index);

			// Sets the objective function value
			solution.setObjectiveFunctionValue(f.getObjectiveFunctionValue(solution.getSolutionRepresentation()));
		}

		System.out.println("AS " + solution.getObjectiveFunctionValue());

		return solution.getObjectiveFunctionValue();
	}

	@Override
	public boolean isCrossover() {

		return false;
	}

	@Override
	public boolean usesIntensityOfMutation() {

		return true;
	}

	@Override
	public boolean usesDepthOfSearch() {

		return false;
	}

}
