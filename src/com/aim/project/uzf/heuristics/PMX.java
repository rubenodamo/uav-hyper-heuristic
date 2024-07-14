package com.aim.project.uzf.heuristics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.aim.project.uzf.UZFObjectiveFunction;
import com.aim.project.uzf.interfaces.ObjectiveFunctionInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;
import com.aim.project.uzf.interfaces.XOHeuristicInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class PMX implements XOHeuristicInterface {

	private final Random random;

	private ObjectiveFunctionInterface f;

	public PMX(Random random) {

		this.random = random;
	}

	@Override
	public int apply(UAVSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		// n/a
		return solution.getObjectiveFunctionValue();
	}

	@Override
	public double apply(UAVSolutionInterface p1, UAVSolutionInterface p2, UAVSolutionInterface c, double depthOfSearch, double intensityOfMutation) {

		// Creates int arrays for the parents and child
		int[] parent1 = p1.getSolutionRepresentation().getSolutionRepresentation();
		int[] parent2 = p2.getSolutionRepresentation().getSolutionRepresentation();
		int[] child = c.getSolutionRepresentation().getSolutionRepresentation();

		// Create two random cut points to generate the subsequence
		int c_point1 = random.nextInt(parent1.length);
		int c_point2 = random.nextInt(parent1.length);

		// If the cut points are in the wrong order, swap
		if (c_point1 > c_point2) {
			int temp = c_point1;
			c_point1 = c_point2;
			c_point2 = temp;
		}

		// Copy the segment from parent1 to the child
        if (c_point2 + 1 - c_point1 >= 0) {
			System.arraycopy(parent1, c_point1, child, c_point1, c_point2 + 1 - c_point1);
		}

		// Create a mapping of locations from parent2 to parent1 within the selected segment
		Map<Integer, Integer> mapping = new HashMap<>();
		for (int i = c_point1; i <= c_point2; i++) {
			mapping.put(parent1[i], parent2[i]);
		}

		// Fill in the remaining child from parent2, preserving mappings
		for (int i = 0; i < child.length; i++) {

			// If the index is in the segment...
			if (i >= c_point1 && i <= c_point2) {
				continue; // Skip the segment copied from parent1
			}
			// Direct copy
			int location = parent2[i];

			// While loop to check if a mapping exists
			while (mapping.containsKey(location)) {
				location = mapping.get(location);
			}
			// Copies location to child
			child[i] = location;
		}

		// Sets the objective function value of the child
		c.setObjectiveFunctionValue(f.getObjectiveFunctionValue(c.getSolutionRepresentation()));
		System.out.println("PMX " + c.getObjectiveFunctionValue());
		return c.getObjectiveFunctionValue();
	}

	@Override
	public void setObjectiveFunction(ObjectiveFunctionInterface f) {

		this.f = f;
	}

	@Override
	public boolean isCrossover() {

		return true;
	}

	@Override
	public boolean usesIntensityOfMutation() {

		return false;
	}

	@Override
	public boolean usesDepthOfSearch() {

		return false;
	}
}
