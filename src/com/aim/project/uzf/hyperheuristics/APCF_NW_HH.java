package com.aim.project.uzf.hyperheuristics;

import com.aim.project.uzf.UZFDomain;
import com.aim.project.uzf.SolutionPrinter;
import com.aim.project.uzf.interfaces.UAVSolutionInterface;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * <h1> Adaptive Choice Function (Non-Worsening) Hyper-Heuristic</h1>
 *
 * This class implements a hyper-heuristic (HH) using an adaptive choice function (CF) approach.
 * The choice function combines two measures: the heuristic's rank in terms of performance and
 * the time since the last application of the heuristic. The class selects and applies low-level
 * heuristics based on their choice function values to search for optimal solutions efficiently.
 * <br>
 * <br>
 * Choice Function:
 * {@code CF(h) = alpha * f_1(h) + f_2(h)}
 * <br>
 * <br>
 * Pseudocode Logic:
 * <pre>{@code
 * 1:    procedure HH_CFH where H is a set of the low level heuristics.
 * 2:    Initialization
 * 3:    Run h, ∀ h ∈ H
 * 4:    Rank h, ∀ h ∈ H based on the ranking scheme
 * 5:    Get CF(h), ∀ h ∈ H
 * 6:    Select h with the largest CF(h) as an initial heuristic
 * 7:    Execute the selected h and produce a front A
 * 8:    repeat
 * 9:    Update the rank of h, ∀ h ∈ H based on the ranking scheme
 * 10:   Update CF(h), ∀ h ∈ H
 * 11:   Select h with the largest CF(h), ∀ h ∈ H
 * 12:   Execute the selected h and produce a new front B
 * 13:   Call the acceptance procedure GDA(A,B)/LA(A,B)
 * 14:   until (termination criteria are satisfied)
 * 15:   end procedure
 * }</pre>
 */
public class APCF_NW_HH extends HyperHeuristic {

    private static final int BEST_ACCEPTED_INDEX = 3;

    private double[] heuristicObjectives;
    private double[] cfValues;
    private final double alpha;
    private final double gamma;

    /**
     * Constructs a new instance of the hyper-heuristic.
     *
     * @param lSeed the seed for random number generation.
     * @param alpha the coefficient for the first measure in the choice function.
     * @param gamma the coefficient for the second measure in the choice function.
     */
    public APCF_NW_HH(long lSeed, double alpha, double gamma) {
        super(lSeed);
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    protected void solve(ProblemDomain oProblem) {
        // Initialization
        int numberOfHeuristics = oProblem.getNumberOfHeuristics();
        oProblem.setMemorySize(4);
        int currentIndex = 0;
        int candidateIndex = 1;
        oProblem.initialiseSolution(currentIndex);
        oProblem.copySolution(currentIndex, BEST_ACCEPTED_INDEX);

        heuristicObjectives = new double[numberOfHeuristics];
        cfValues = new double[numberOfHeuristics];
        long lastHeuristicApplicationTime = System.currentTimeMillis(); // Initialize lastHeuristicApplicationTime

        // Cache indices of crossover heuristics
        boolean[] isCrossover = new boolean[numberOfHeuristics];
        Arrays.fill(isCrossover, false);

        for (int i : oProblem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER)) {
            isCrossover[i] = true;
        }

        // Run each low-level heuristic multiple times to gather statistics for choice function
        int runsPerHeuristic = 100; // Adjust as needed
        for (int heuristicIndex = 0; heuristicIndex < numberOfHeuristics; heuristicIndex++) {
            double totalCost = 0;
            for (int run = 0; run < runsPerHeuristic; run++) {
                totalCost += applyHeuristicAndGetCost(oProblem, heuristicIndex, currentIndex, candidateIndex, isCrossover);
            }
            heuristicObjectives[heuristicIndex] = totalCost / runsPerHeuristic; // Average objective value
        }

        // Generate an array of heuristic indices sorted by objective values in ascending order
        int[] rankedHeuristics = IntStream.range(0, numberOfHeuristics)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> heuristicObjectives[i]))
                .mapToInt(Integer::intValue)
                .toArray();

        // Calculate choice function values for each heuristic
        for (int i = 0; i < rankedHeuristics.length; i++) {
            int heuristicIndex = rankedHeuristics[i];
            double f1 = calculateF1(heuristicIndex, rankedHeuristics);
            double f2 = calculateF2(lastHeuristicApplicationTime);
            cfValues[i] = alpha * f1 + gamma * f2; // Using alpha and gamma coefficients
        }

        // Sort the cfValues array in descending order
        Arrays.sort(cfValues);
        // Reverse the array to sort in descending order
        for (int i = 0; i < cfValues.length / 2; i++) {
            double temp = cfValues[i];
            cfValues[i] = cfValues[cfValues.length - 1 - i];
            cfValues[cfValues.length - 1 - i] = temp;
        }

        // Execute the heuristic with the highest choice function value
        int selectedHeuristic = rankedHeuristics[0];
        applyHeuristic(oProblem, selectedHeuristic, currentIndex, candidateIndex, isCrossover);

        // Define an array to store objective values for front A
        double[] objectiveValuesA = new double[numberOfHeuristics];

        // Populate objective values for front A
        for (int i = 0; i < numberOfHeuristics; i++) {
            objectiveValuesA[i] = oProblem.getFunctionValue(currentIndex);
        }

        // Repeat until termination criteria are satisfied
        while (!hasTimeExpired()) {
            // Update CF(h) values for each heuristic
            for (int i = 0; i < rankedHeuristics.length; i++) {
                int heuristic = rankedHeuristics[i];
                double f1 = calculateF1(heuristic, rankedHeuristics);
                double f2 = calculateF2(lastHeuristicApplicationTime);
                cfValues[i] = alpha * f1 + gamma * f2;
            }

            // Sort the cfValues array in descending order
            Arrays.sort(cfValues);
            // Reverse the array to sort in descending order
            for (int i = 0; i < cfValues.length / 2; i++) {
                double temp = cfValues[i];
                cfValues[i] = cfValues[cfValues.length - 1 - i];
                cfValues[cfValues.length - 1 - i] = temp;
            }

            // Execute the heuristic with the highest choice function value
            selectedHeuristic = rankedHeuristics[0];
            applyHeuristic(oProblem, selectedHeuristic, currentIndex, candidateIndex, isCrossover);

            // Produce a new front B
            double[] objectiveValuesB = new double[numberOfHeuristics];
            for (int i = 0; i < numberOfHeuristics; i++) {
                objectiveValuesB[i] = oProblem.getFunctionValue(candidateIndex);
            }

            // If the acceptance procedure accepts the new front B, update the current front A to be the same as B
            if (acceptFrontA(objectiveValuesA, objectiveValuesB)) {
                // Copy objective values from B to A
                System.arraycopy(objectiveValuesB, 0, objectiveValuesA, 0, objectiveValuesA.length);
            }

            // Update last heuristic application time
            lastHeuristicApplicationTime = System.currentTimeMillis();
        }

        // Print the best solution
        UAVSolutionInterface bestSolution = ((UZFDomain) oProblem).getBestSolution();
        SolutionPrinter solutionPrinter = new SolutionPrinter("out.csv");
        solutionPrinter.printSolution(((UZFDomain) oProblem).getLoadedInstance().getSolutionAsListOfLocations(bestSolution));
    }

    // Applies a heuristic to the problem domain and returns the cost of the resulting solution.
    private double applyHeuristicAndGetCost(ProblemDomain oProblem, int heuristicIndex, int currentIndex, int candidateIndex, boolean[] isCrossover) {
        long startTime = System.currentTimeMillis();
        double cost;
        if (isCrossover[heuristicIndex]) {
            oProblem.applyHeuristic(heuristicIndex, currentIndex, BEST_ACCEPTED_INDEX, candidateIndex);
        } else {
            oProblem.applyHeuristic(heuristicIndex, currentIndex, candidateIndex);
        }
        cost = oProblem.getFunctionValue(candidateIndex);
        oProblem.copySolution(candidateIndex, currentIndex); // Reset to the current solution
        long lastHeuristicApplicationTime = System.currentTimeMillis() - startTime;
        return cost;
    }

    // Applies a heuristic to the problem domain.
    private void applyHeuristic(ProblemDomain oProblem, int heuristicIndex, int currentIndex, int candidateIndex, boolean[] isCrossover) {
        if (isCrossover[heuristicIndex]) {
            oProblem.applyHeuristic(heuristicIndex, currentIndex, BEST_ACCEPTED_INDEX, candidateIndex);
        } else {
            oProblem.applyHeuristic(heuristicIndex, currentIndex, candidateIndex);
        }
        oProblem.copySolution(candidateIndex, currentIndex); // Update the current solution
    }

    // Determines whether to accept a new front based on its objective values.
    private boolean acceptFrontA(double[] objectiveValuesA, double[] objectiveValuesB) {
        for (int i = 0; i < objectiveValuesA.length; i++) {
            // Non-worsening acceptance
            if (objectiveValuesA[i] >= objectiveValuesB[i]) {
                return true;
            }
        }
        return false;
    }

    // Calculates the first measure of the choice function for a heuristic.
    private double calculateF1(int h, int[] rankedHeuristics) {
        // Find the position of heuristic h in the ranked heuristics array
        int position = -1;
        for (int i = 0; i < rankedHeuristics.length; i++) {
            if (rankedHeuristics[i] == h) {
                position = i;
                break;
            }
        }
        // If the heuristic is not found in the ranked heuristics array, return 0
        if (position == -1) {
            return 0.0;
        }
        // Otherwise, return the performance based on the position (e.g., inverse of position)
        return 1.0 / (position + 1); // Adding 1 to avoid division by zero
    }

    // Calculates the second measure of the choice function for a heuristic.
    private double calculateF2(long lastHeuristicApplicationTime) {
        // Return the elapsed time
        return lastHeuristicApplicationTime / 1000.0; // Convert milliseconds to seconds
    }

    @Override
    public String toString() {
        return "APCF_NW_HH";
    }
}