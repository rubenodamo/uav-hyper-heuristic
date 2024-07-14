package com.aim.project.uzf.solution;

import com.aim.project.uzf.interfaces.SolutionRepresentationInterface;

/**
 * Represents a solution representation for the UZF problem.
 * Stores an array representing the solution representation.
 *
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class SolutionRepresentation implements SolutionRepresentationInterface {

	private int[] aiRepresentation;

	public SolutionRepresentation(int[] aiRepresentation) {

		this.aiRepresentation = aiRepresentation;
	}

	// Gets the solution representation array
	@Override
	public int[] getSolutionRepresentation() {

		return this.aiRepresentation;
	}

	// Sets the solution representation array
	@Override
	public void setSolutionRepresentation(int[] aiSolutionRepresentation) {

		this.aiRepresentation = aiSolutionRepresentation;
	}

	// Gets the number of locations in the solution representation
	@Override
	public int getNumberOfLocations() {

		return this.aiRepresentation.length;
	}


	// Deep clone of the SolutionRepresentation
	@Override
	public SolutionRepresentationInterface clone() {
		// Create a new instance of SolutionRepresentation with the same representation (deep clone)
		return new SolutionRepresentation(this.aiRepresentation.clone());
	}

}
