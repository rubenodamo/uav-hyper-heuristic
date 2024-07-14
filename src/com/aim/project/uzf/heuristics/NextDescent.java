package com.aim.project.uzf.heuristics;


import java.util.Random;

import com.aim.project.uzf.interfaces.HeuristicInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;


/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class NextDescent extends HeuristicOperators implements HeuristicInterface {

	public NextDescent(Random random) {
	
		super(random);
	}

	@Override
	public int apply(UAVSolutionInterface solution, double dos, double iom) {

		// Gets the solution representation to an array
		int[] solutionRepresentation = solution.getSolutionRepresentation().getSolutionRepresentation();
		// Calculate the internal number of iterations based on DOS in [0,1]
		int internalIterations = (int) (Math.floor(dos/0.2)+1);

		// Get a random start point, to start iterating from
		int startPoint = random.nextInt(solutionRepresentation.length-1);

		for (int i = 0; i < internalIterations; i++) {

			// Gets the current cost
			double currentCost = solution.getObjectiveFunctionValue();
			// Flag to determine improvement
			boolean improved = false;

			// Iterate through each enclosure
			for (int  j = 0; j < solutionRepresentation.length; j++) {

				// Perform swap and calculate the candidate
				swapAdjacent(solutionRepresentation, ((startPoint + j) % (solutionRepresentation.length-1)));
				double candidateCost = f.getObjectiveFunctionValue(solution.getSolutionRepresentation());

				// Accept improving moves only (strict improvement)
				if (candidateCost < currentCost) {
					// Accept - set improvement to true
					improved = true;
					startPoint = (startPoint + j+1) % (solutionRepresentation.length-1); // alter start point
					// Set the objective function value
					solution.setObjectiveFunctionValue(f.getObjectiveFunctionValue(solution.getSolutionRepresentation()));
					break; // break
				}
				else {
					// Reject
					swapAdjacent(solutionRepresentation, (startPoint + j) % (solutionRepresentation.length-1));
				}

			}

			// If no improvement is found, break
			if (!improved) {
				break;
			}


		}

		System.out.println("ND " + solution.getObjectiveFunctionValue());
		return solution.getObjectiveFunctionValue();
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
