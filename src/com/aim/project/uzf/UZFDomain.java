package com.aim.project.uzf;

import com.aim.project.uzf.heuristics.*;
import com.aim.project.uzf.instance.InitialisationMode;
import com.aim.project.uzf.instance.Location;
import com.aim.project.uzf.instance.reader.UAVInstanceReader;
import com.aim.project.uzf.interfaces.*;

import AbstractClasses.ProblemDomain;
import com.aim.project.uzf.solution.UZFSolution;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class UZFDomain extends ProblemDomain implements Visualisable {


	public UZFInstanceInterface instance;
	public List<HeuristicInterface> heuristics;

	private UAVSolutionInterface bestSolution;
	private UZFSolution[] solutionMemory = new UZFSolution[2];

    public UZFDomain(long seed) {
		// Set default memory size and create the array of low-level heuristics
		super(seed);

		// Initialise the ArrayList of heuristics
		this.heuristics = new ArrayList<>();

		/* Add heuristics - {0 : Adjacent Swap, 1 : DHC, 2 : Next Descent, 3 : PMX - Partially Mapped Crossover,
			4 : Reinsertion, 5 : Inversion, 6 : Steepest Descent, 7 : CX - Cycle Crossover } */
		this.heuristics.add(new AdjacentSwap(super.rng));
		this.heuristics.add(new DavissHillClimbing(super.rng));
		this.heuristics.add(new NextDescent(super.rng));
		this.heuristics.add(new PMX(super.rng));
		this.heuristics.add(new Reinsertion(super.rng));
		this.heuristics.add(new Inversion(super.rng));
		this.heuristics.add(new SteepestDescentHC(super.rng));
		this.heuristics.add(new CX(super.rng));
	}

	// Apply heuristic and return the objective value of the candidate solution
	@Override
	public double applyHeuristic(int hIndex, int currentIndex, int candidateIndex) {

		// Retrieve the heuristic based on the index
		HeuristicInterface heuristic = heuristics.get(hIndex);

		// If the current index is different from the candidate index, copy the solution
		if (currentIndex != candidateIndex) {
			copySolution(currentIndex, candidateIndex);
		}

		// Record the start time and end time
		// Apply the heuristic to the candidate solution and get the objective value
		long startTime = System.currentTimeMillis();
		double oSolutionValue = heuristic.apply(solutionMemory[candidateIndex], depthOfSearch, intensityOfMutation);
		long endTime = System.currentTimeMillis();

		// Update the best solution based on the candidate index
		updateBestSolution(candidateIndex);
		// Increment the heuristic call count and record the time taken
		super.heuristicCallRecord[hIndex]++;
		super.heuristicCallTimeRecord[hIndex] += (int) (endTime - startTime);

		// Return the objective value of the candidate solution
		return oSolutionValue;
	}

	// Apply heuristic and return the objective value of the candidate solution
	@Override
	public double applyHeuristic(int hIndex, int parent1Index, int parent2Index, int candidateIndex) {

		// Retrieve the heuristic based on the index
		HeuristicInterface heuristic1 = heuristics.get(hIndex);

		// If the candidate solution is null, initialise it
		if (solutionMemory[candidateIndex] == null) {
			initialiseSolution(candidateIndex);
		}

		// Cast the heuristic to XOHeuristicInterface
		XOHeuristicInterface heuristic2 = (XOHeuristicInterface) heuristic1;

		// Record the start and end time
		// Apply the crossover heuristic to the parent solutions and get the objective value
		long startTime = System.currentTimeMillis();
		double oSolutionValue = heuristic2.apply(solutionMemory[parent1Index], solutionMemory[parent2Index],
				solutionMemory[candidateIndex], depthOfSearch, intensityOfMutation);
		long endTime = System.currentTimeMillis();

		// Update the best solution based on the candidate index
		updateBestSolution(candidateIndex);
		// Increment the heuristic call count and record the time taken
		super.heuristicCallRecord[hIndex]++;
		super.heuristicCallTimeRecord[hIndex] += (int) (endTime - startTime);

		// Return the objective value of the candidate solution
		return oSolutionValue;
	}

	// Converts best solution to string
	@Override
	public String bestSolutionToString() {

		// Get the representation of the best solution as an array of integers
		int[] bestRepresentation = this.bestSolution.getSolutionRepresentation().getSolutionRepresentation();
		// Convert the array to a string and return
		return Arrays.toString(bestRepresentation);
	}

	// Compares solutions, checking if they are equal
	@Override
	public boolean compareSolutions(int a, int b) {

		// Get the solution representations for the specified indices
		int[] aiSolutionA = solutionMemory[a].getSolutionRepresentation().getSolutionRepresentation();
		int[] aiSolutionB = solutionMemory[b].getSolutionRepresentation().getSolutionRepresentation();

		// Compare the two solution representations and return the result
		return Arrays.equals(aiSolutionA, aiSolutionB);
	}

	// Copies solution from one index to another
	@Override
	public void copySolution(int a, int b) {

		// BEWARE this should copy the solution, not the reference to it!
		//			That is, that if we apply a heuristic to the solution in index 'b',
		//			then it does not modify the solution in index 'a' or vice-versa.

		// clone the solution from index 'a' to index 'b'
		solutionMemory[b] = (UZFSolution) this.solutionMemory[a].clone();
	}

	// Get the objective function value of the best solution
	@Override
	public double getBestSolutionValue() {

		return this.bestSolution.getObjectiveFunctionValue();
	}

	// Get the objective function value of the solution at the specified index
	@Override
	public double getFunctionValue(int index) {

		return this.solutionMemory[index].getObjectiveFunctionValue();
	}

	// Returns the heuristic types
	@Override
	public int[] getHeuristicsOfType(HeuristicType type) {

		// Return the indices of heuristics based on their types
		return switch(type) {
			case CROSSOVER -> IntStream.range(0, getNumberOfHeuristics()).filter(h ->
					heuristics.get(h).isCrossover()).toArray();
			case LOCAL_SEARCH -> getHeuristicsThatUseDepthOfSearch();
			case MUTATION -> getHeuristicsThatUseIntensityOfMutation();
			case RUIN_RECREATE, OTHER -> null;
        };
	}


	// Return the indices of heuristics that use depth of search
	@Override
	public int[] getHeuristicsThatUseDepthOfSearch() {
		
		return IntStream.range(0, getNumberOfHeuristics()).filter(h ->
				heuristics.get(h).usesDepthOfSearch()).toArray();
	}

	// Return the indices of heuristics that use intensity of mutation
	@Override
	public int[] getHeuristicsThatUseIntensityOfMutation() {

		return IntStream.range(0, getNumberOfHeuristics()).filter(h ->
				heuristics.get(h).usesIntensityOfMutation()).toArray();
	}

	// Return the total number of heuristics
	@Override
	public int getNumberOfHeuristics() {

		// has to be hard-coded due to the design of the HyFlex framework
		return 8;
	}

	// Return the total number of instances
	@Override
	public int getNumberOfInstances() {

		// has to be hard-coded due to the design of the HyFlex framework
		return 7;
	}

	// Initialises a solution at a specified index
	@Override
	public void initialiseSolution(int index) {
		
		// Make sure that you also update the best solution!
		// Creates solution using the RANDOM or CONSTRUCTIVE mode
		solutionMemory[index] = instance.createSolution(InitialisationMode.CONSTRUCTIVE);
		updateBestSolution(index);
	}

	@Override
	public void loadInstance(int instanceId) {
		// Load the instance (referenced by ID) from file
		String file = "";
		switch (instanceId) {
			case 0: {
				file = "instances/uzf/square.uzf";
				break;
			}
			case 1: {
				file = "instances/uzf/libraries-15.uzf";
				break;
			}
			case 2: {
				file = "instances/uzf/carparks-40.uzf";
				break;
			}
			case 3: {
				file = "instances/uzf/tramstops-85.uzf";
				break;
			}
			case 4: {
				file = "instances/uzf/grid.uzf";
				break;
			}
			case 5: {
				file = "instances/uzf/clustered-enclosures.uzf";
				break;
			}
			case 6: {
				file = "instances/uzf/chatgpt-instance-100-enclosures.uzf";
				break;
			}
			default: {
				System.err.println("UZF Domain does not support instance id " + instanceId);
				System.exit(-1);
			}
		}

		System.out.println("Calling read: "+file);
		UAVInstanceReader reader = new UAVInstanceReader();
		instance = reader.readUZFInstance(Path.of(file), rng);

		// Set the objective function within each low-level heuristic
        ObjectiveFunctionInterface function = instance.getUZFObjectiveFunction();
		for (HeuristicInterface heuristic : heuristics) {
			heuristic.setObjectiveFunction(function);
		}
	}

	// Sets the solution memory size
	@Override
	public void setMemorySize(int size) {
		// Create a temporary array to hold the updated solution memory
		UZFSolution[] tempMemory = new UZFSolution[size];

		// Copy the existing solution memory to the temporary array, up to the minimum of either the current size or the new size
		if(this.solutionMemory != null) {
			System.arraycopy(this.solutionMemory, 0, tempMemory, 0,
					Math.min(this.solutionMemory.length, size));
		}

		// Update the solution memory reference to point to the temporary array
		this.solutionMemory = tempMemory;
	}

	// Converts solution to string
	@Override
	public String solutionToString(int index) {

		// Get the solution representations for the specified indices
		int[] solutionRepresentation = this.solutionMemory[index].getSolutionRepresentation().getSolutionRepresentation();
		return Arrays.toString(solutionRepresentation);
	}

	@Override
	public String toString() {

		return "UZF";
	}

	// Updates the best solution
	private void updateBestSolution(int index) {
		// Make sure we cannot modify the best solution accidentally after storing it!

		// If the best solution is not better than the current solution, update it
		if (this.bestSolution == null ||
				this.solutionMemory[index].getObjectiveFunctionValue() < this.getBestSolutionValue()) {
			this.bestSolution = this.solutionMemory[index];
		}
	}

	@Override
	public UZFInstanceInterface getLoadedInstance() {

		return this.instance;
	}

	/**
	 * @return The integer array representing the ordering of the best solution.
	 */
	@Override
	public int[] getBestSolutionRepresentation() {

		return this.bestSolution.getSolutionRepresentation().getSolutionRepresentation();
	}

	@Override
	public Location[] getRouteOrderedByLocations() {
		// Solution representation of best solution
		int[] solutionRepresentation = this.bestSolution.getSolutionRepresentation().getSolutionRepresentation();

		// Create an array of Location objects with the same length as solutionRepresentation array
		Location[] route = new Location[solutionRepresentation.length];

		// Iterate over each element in the solutionRepresentation array
		for (int i = 0; i < solutionRepresentation.length; i++) {
			// Retrieve the Location object corresponding to the current solutionRepresentation index
			// and store it in the route array
			route[i] = this.getLoadedInstance().getLocationForEnclosure(solutionRepresentation[i]);
		}

		return route;
	}

	public UAVSolutionInterface getBestSolution() {

		return bestSolution;
	}
}
