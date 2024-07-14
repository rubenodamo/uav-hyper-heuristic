package com.aim.project.uzf.solution;

import com.aim.project.uzf.interfaces.UAVSolutionInterface;
import com.aim.project.uzf.interfaces.SolutionRepresentationInterface;

/**
 * Represents a solution for the UZF problem.
 * Stores the solution representation and the objective function value.
 *
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class UZFSolution implements UAVSolutionInterface {


	SolutionRepresentationInterface representation; // Store the solution representation
	private int objectFunctionValue; // Store the object function value
	
	public UZFSolution(SolutionRepresentationInterface representation, int objectiveFunctionValue) {
		this.representation = representation;
		this.objectFunctionValue = objectiveFunctionValue;
	}


	// Gets the object function value of the solution
	@Override
	public int getObjectiveFunctionValue() {

		return this.objectFunctionValue;
	}

	// Sets the objective function value of the solution
	@Override
	public void setObjectiveFunctionValue(int objectiveFunctionValue) {

		this.objectFunctionValue = objectiveFunctionValue;
	}

	// Gets the solution representation
	@Override
	public SolutionRepresentationInterface getSolutionRepresentation() {

		return this.representation;
	}

	// Clones the UZFSolution
	@Override
	public UAVSolutionInterface clone() {
		// Create a new instance of UZFSolution with the same representation and objective function value
		return new UZFSolution(this.representation.clone(), this.objectFunctionValue);
	}

	// Gets the number of locations in the solution representation
	@Override
	public int getNumberOfLocations() {
		// Return the number of locations in the solution representation
		return this.representation.getNumberOfLocations();
	}
}
