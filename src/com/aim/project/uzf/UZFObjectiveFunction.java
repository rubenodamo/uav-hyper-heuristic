package com.aim.project.uzf;

import com.aim.project.uzf.instance.Location;
import com.aim.project.uzf.interfaces.ObjectiveFunctionInterface;
import com.aim.project.uzf.interfaces.UZFInstanceInterface;
import com.aim.project.uzf.interfaces.SolutionRepresentationInterface;


/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class UZFObjectiveFunction implements ObjectiveFunctionInterface {

	private final UZFInstanceInterface oInstance;

	public UZFObjectiveFunction(UZFInstanceInterface oInstance) {
		this.oInstance = oInstance;
	}

	@Override
	public int getObjectiveFunctionValue(SolutionRepresentationInterface oSolution) {

		int[] solutionRepresentation = oSolution.getSolutionRepresentation();
		int totalDistance = 0;

		// Add the distance between the first location and food prep area
		totalDistance += getCostBetweenFoodPreparationAreaAnd(solutionRepresentation[0]);

		// FOR loop to calculate remaining distances
		for (int i = 0; i < solutionRepresentation.length - 1 ; i++) {
			int locationA = solutionRepresentation[i];
			int locationB = solutionRepresentation[i + 1];

			totalDistance += getCost(locationA,	locationB);
		}

		// Add the distance between the last location and food prep area
		int finalLocation = solutionRepresentation[solutionRepresentation.length - 1];
		totalDistance += getCostBetweenFoodPreparationAreaAnd(finalLocation);

		return totalDistance;
	}
	
	public int getCost(Location oLocationA, Location oLocationB) {

		// Calculate distance between locations
		double dx = oLocationA.x() - oLocationB.x();
		double dy = oLocationA.y() - oLocationB.y();

		return (int) Math.ceil(Math.sqrt((dx * dx) + (dy * dy)));
	}

	@Override
	public int getCost(int iLocationA, int iLocationB) {

		// Retrieve the Location objects corresponding to the given location IDs
		Location oLocationA = oInstance.getLocationForEnclosure(iLocationA);
		Location oLocationB = oInstance.getLocationForEnclosure(iLocationB);

		return this.getCost(oLocationA, oLocationB);
	}

	@Override
	public int getCostBetweenFoodPreparationAreaAnd(int iLocation) {

		// Retrieve the Location objects corresponding to first location and food prep area
		Location oLocation = oInstance.getLocationForEnclosure(iLocation);
		Location oFoodPreparationArea = oInstance.getLocationOfFoodPreparationArea();
		
		return getCost(oLocation, oFoodPreparationArea);
	}

}
