package com.aim.project.uzf.heuristics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.aim.project.uzf.interfaces.HeuristicInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;


/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class DavissHillClimbing extends HeuristicOperators implements HeuristicInterface {
	

	
	public DavissHillClimbing(Random random) {
	
		super(random);
	}

	@Override
	public int apply(UAVSolutionInterface solution, double dos, double iom) {

		// Gets the solution representation to an array
		int[] solutionRepresentation = solution.getSolutionRepresentation().getSolutionRepresentation();
		// Calculate the internal number of iterations based on DOS in [0,1]
		int internalIterations = (int) (Math.floor(dos/0.2)+1);

		for(int i = 0; i < internalIterations; i++) {

			// Get the ordered locations
			int[] orderedIndicies = IntStream.range(0, solution.getNumberOfLocations()-1).toArray();
			// Create a random permuatation
			int[] perm =  createRandomPermutation(orderedIndicies, this.random);

			// Gets the current cost
			double currentCost = solution.getObjectiveFunctionValue();

			// Iterate through each enclosure in the permutation
			for (int j = 0; j < perm.length; j++) {
				// Perform swap
				swapAdjacent(solutionRepresentation, perm[j]);
				// Gets the candidate cost
				double candidateCost = f.getObjectiveFunctionValue(solution.getSolutionRepresentation());

				// only accepts improving moves (strict improvement)
				if (candidateCost < currentCost) {
					// accepts
					solution.setObjectiveFunctionValue(f.getObjectiveFunctionValue(solution.getSolutionRepresentation()));
					currentCost = candidateCost; // Reassigns current cost
				}
				else {
					// reject
					swapAdjacent(solutionRepresentation, perm[j]);
				}
			}

		}

		System.out.println("DBHC " + solution.getObjectiveFunctionValue());
		return solution.getObjectiveFunctionValue();
	}

	// Creates a random permutation of a given array
	public static int[] createRandomPermutation(int[] array, Random random) {

		int[] shuffledArray = new int[array.length];
		System.arraycopy(array, 0, shuffledArray,0, array.length);

		for(int i = 0; i < shuffledArray.length; i++) {
			//swap ith index with random index
			int index = random.nextInt(shuffledArray.length);
			int temp = shuffledArray[i];
			shuffledArray[i] = shuffledArray[index];
			shuffledArray[index] = temp;
		}

		return shuffledArray;
	}

	@Override
	public boolean isCrossover() {

		return false;
	}

	@Override
	public boolean usesIntensityOfMutation() {

		return false;
	}

	@Override
	public boolean usesDepthOfSearch() {

		return true;
	}
}
