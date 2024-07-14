package com.aim.project.uzf.instance;


import java.util.ArrayList;
import java.util.Random;

import com.aim.project.uzf.UZFObjectiveFunction;
import com.aim.project.uzf.interfaces.ObjectiveFunctionInterface;
import com.aim.project.uzf.interfaces.UZFInstanceInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;
import com.aim.project.uzf.solution.SolutionRepresentation;
import com.aim.project.uzf.solution.UZFSolution;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class UZFInstance implements UZFInstanceInterface {

	private final int numberOfLocations;
	private final Location[] aoLocations;
	private final Location foodPreparationLocation;
	private final Random random;
	
	public UZFInstance(int numberOfLocations, Location[] aoLocations, Location foodPreparationLocation, Random random) {
		this.numberOfLocations = numberOfLocations;
		this.aoLocations = aoLocations;
		this.foodPreparationLocation = foodPreparationLocation;
		this.random = random;
	}

	// Create a solution for the instance
	@Override
	public UZFSolution createSolution(InitialisationMode mode) {
		int[] solutionRepresentationArray = new int[numberOfLocations];

		if (mode ==  InitialisationMode.RANDOM) {
			// Generate a random solution representation
			for (int i = 0; i < numberOfLocations; i++) {
				solutionRepresentationArray[i] = i;
			}
			shuffleArray(solutionRepresentationArray, random);

		}
		else if (mode == InitialisationMode.CONSTRUCTIVE) {
			// Constructive initialisation using nearest neighbor greedy algorithm
			boolean[] visited = new boolean[numberOfLocations];
			solutionRepresentationArray[0] = new Random().nextInt(0, numberOfLocations); // Start from pseudo random location
			visited[solutionRepresentationArray[0]] = true;

			for (int i = 1; i < numberOfLocations; i++) {
				int nearestNeighbor = getNearestNeighbor(solutionRepresentationArray, i, visited);
				solutionRepresentationArray[i] = nearestNeighbor;
				visited[nearestNeighbor] = true;
			}

		}
		SolutionRepresentation solutionRepresentation = new SolutionRepresentation(solutionRepresentationArray);
		
		return new UZFSolution(solutionRepresentation, 
				this.getUZFObjectiveFunction().getObjectiveFunctionValue(solutionRepresentation));
	}

	// Get objective function of the instance
	@Override
	public ObjectiveFunctionInterface getUZFObjectiveFunction() {

		return new UZFObjectiveFunction(this);
	}

	// Gets the number of locations
	@Override
	public int getNumberOfLocations() {

		return numberOfLocations;
	}

	// Gets the location for the given enclosure ID
	@Override
	public Location getLocationForEnclosure(int iEnclosureId) {

		return this.aoLocations[iEnclosureId];
	}

	// Gets the location for the food preparation area
	@Override
	public Location getLocationOfFoodPreparationArea() {

		return this.foodPreparationLocation;
	}

	// Gets the solution as a list of locations
	@Override
	public ArrayList<Location> getSolutionAsListOfLocations(UAVSolutionInterface oSolution) {

		ArrayList<Location> solutionLocations = new ArrayList<>();
		int[] solutionRepresentation = oSolution.getSolutionRepresentation().getSolutionRepresentation();

        for (int index : solutionRepresentation) {
            solutionLocations.add(aoLocations[index]);
        }

		return solutionLocations;
	}


	// Helper method to get nearest neighbour greedy algorithm
	private int getNearestNeighbor(int[] solutionRepresentationArray, int i, boolean[] visited) {
		int nearestNeighbor = -1;
		double minDistance = Double.MAX_VALUE;
		Location currentLocation = aoLocations[solutionRepresentationArray[i - 1]];

		for (int j = 0; j < this.numberOfLocations; j++) {
			if (!visited[j]) {
				UZFObjectiveFunction objectiveFunction = (UZFObjectiveFunction) this.getUZFObjectiveFunction();
				double distance = objectiveFunction.getCost(currentLocation, aoLocations[j]);
				if (distance < minDistance) {
					minDistance = distance;
					nearestNeighbor = j;
				}
			}
		}
		return nearestNeighbor;
	}

	// Helper method to shuffle an array randomly
	private void shuffleArray(int[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Performs the swap
			int temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}

}
