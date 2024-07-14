package com.aim.project.uzf.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.uzf.interfaces.ObjectiveFunctionInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 * <br>
 * This class is included (and all non-crossover heuristics subclass this class) to simplify your implementation and it
 * is intended that you include any common operations in this class to simplify your implementation of the other heuristics.
 * Furthermore, if you implement and test common functionality here, it is less likely that you introduce a bug elsewhere!
 * <br>
 * For example, think about common neighbourhood operators and any other incremental changes that you might perform
 * while applying low-level heuristics.
 */
public class HeuristicOperators {

	protected ObjectiveFunctionInterface f;

	protected final Random random;

	public HeuristicOperators(Random random) {

		this.random = random;
	}

	// Function to swap adjacent enclosures
	protected void swapAdjacent(int[] array, int index) {
		int temp = array[index];
		array[index] = array[((index + 1)% array.length)];
		array[((index + 1)% array.length)] = temp;
	}

	// Function to reinsert enclosures
	protected void reinsertion(int[] solutionRepresentation, int r_index, int i_index) {
		// Removes the element
		int removedElement = solutionRepresentation[r_index];
		System.arraycopy(solutionRepresentation, r_index+1, solutionRepresentation, r_index,
				solutionRepresentation.length - 1 - r_index);

		// Insert the element
		System.arraycopy(solutionRepresentation, i_index, solutionRepresentation, i_index + 1,
				solutionRepresentation.length - i_index - 1);
		solutionRepresentation[i_index] = removedElement;
	}

	protected void invert(int[] solutionRepresentation, int i_point1, int i_point2) {
		// Perform inversion
		while (i_point1 < i_point2) {
			int temp = solutionRepresentation[i_point1];
			solutionRepresentation[i_point1] = solutionRepresentation[i_point2];
			solutionRepresentation[i_point2] = temp;
			i_point1++;
			i_point2--;
		}

	}

	// Sets objective function
	public void setObjectiveFunction(ObjectiveFunctionInterface f) {

		this.f = f;
	}
}
