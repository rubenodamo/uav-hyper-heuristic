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
public class CX implements XOHeuristicInterface {

    private final Random random;

    private ObjectiveFunctionInterface f;

    public CX(Random random) {

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

        // Randomly select a starting point
        int startIndex = random.nextInt(parent1.length);
        // Boolean visited array
        boolean[] visited = new boolean[child.length];
        // Set the child value at the start index to the same as parent 1, mark as visited
        child[startIndex] = parent1[startIndex];
        visited[startIndex] = true;

        // Create a mapping of locations from parent1 to parent2 within the selected segment
        Map<Integer, Integer> mapping = new HashMap<>();
        for (int i = 0; i < parent1.length; i++) {
            mapping.put(parent1[i], parent2[i]);
        }

        // Obtain the parent 2 value from the parent 1 mapping
        int p2_location = mapping.get(parent1[startIndex]);
        // Set the next index based on parent 2 location in parent 1
        int p1_index = getIndexOf(parent1, p2_location);

        // Start cycle
        while (p1_index != startIndex) {
            // Set the child value at the parent 1 index to the same as parent 2, mark as visited
            child[p1_index] = p2_location;
            visited[p1_index] = true;

            // Obtain the parent 2 value from the parent 1 mapping
            p2_location = mapping.get(parent1[p1_index]);
            // Set the next index based on parent 2 location in parent 1
            p1_index = getIndexOf(parent1, p2_location);
        }

        // Cycle complete
        for (int i = 0; i < child.length; i++) {
            if (!visited[i]) {
               child[i] = parent2[i];
            }
        }


        // Sets the objective function value of the child
        c.setObjectiveFunctionValue(f.getObjectiveFunctionValue(c.getSolutionRepresentation()));
        System.out.println("CX " + c.getObjectiveFunctionValue());
        return c.getObjectiveFunctionValue();
    }

    // Helper function to get the index of a given value
    private int getIndexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i; // Return the index if the value is found
            }
        }
        return -1; // Return -1 if the value is not found in the array
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
