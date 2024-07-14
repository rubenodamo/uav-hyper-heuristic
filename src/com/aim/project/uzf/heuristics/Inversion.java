package com.aim.project.uzf.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.uzf.interfaces.HeuristicInterface;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class Inversion extends HeuristicOperators implements HeuristicInterface {


    public Inversion(Random random) {

        super(random);
    }

    @Override
    public int apply(UAVSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

        // Gets the solution representation to an array
        int[] solutionRepresentation = solution.getSolutionRepresentation().getSolutionRepresentation();
        // Calculate the internal number of reinsertions based on IOM in [0,1]
        int inversionNumber = (int) (Math.floor(intensityOfMutation/0.2)+1);

        for (int i = 0; i < inversionNumber; i++) {
            // Generate random indexes to remove and insert to
            int inversionPoint1 = random.nextInt(solution.getNumberOfLocations());
            int inversionPoint2 = random.nextInt(solution.getNumberOfLocations());

            // If the indexes are equal generate another random index to invert to
            while (inversionPoint1 == inversionPoint2) {
                inversionPoint2 = random.nextInt(solution.getNumberOfLocations());
            }
            // If the inversion points are in the wrong order, swap
            if (inversionPoint1 > inversionPoint2) {
                int temp = inversionPoint1;
                inversionPoint1 = inversionPoint2;
                inversionPoint2 = temp;
            }

            // Perform inversion
            invert(solutionRepresentation, inversionPoint1, inversionPoint2);

            // Sets the objective function value
            solution.setObjectiveFunctionValue(f.getObjectiveFunctionValue(solution.getSolutionRepresentation()));
        }

        System.out.println("INV " + solution.getObjectiveFunctionValue());
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
