package com.aim.project.uzf.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.uzf.interfaces.HeuristicInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class Reinsertion extends HeuristicOperators implements HeuristicInterface {


	public Reinsertion(Random random) {

		super(random);
	}

	@Override
	public int apply(UAVSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		// Gets the solution representation to an array
		int[] solutionRepresentation = solution.getSolutionRepresentation().getSolutionRepresentation();
		// Calculate the internal number of reinsertions based on IOM in [0,1]
		int reinsertionNumber = (int) (Math.floor(intensityOfMutation/0.2)+1);

		for (int i = 0; i < reinsertionNumber; i++) {
			// Generate random indexes to remove and insert to
			int removeIndex = random.nextInt(solution.getNumberOfLocations());
			int insertionIndex = random.nextInt(solution.getNumberOfLocations());

			// If the indexes are equal generate another random index to insert into
			while (removeIndex == insertionIndex) {
				insertionIndex = random.nextInt(solution.getNumberOfLocations());
			}
			// Perform reinsertion
			reinsertion(solutionRepresentation, removeIndex, insertionIndex);

			// Sets the objective function value
			solution.setObjectiveFunctionValue(f.getObjectiveFunctionValue(solution.getSolutionRepresentation()));
		}

		System.out.println("REINSERTION " + solution.getObjectiveFunctionValue());
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
