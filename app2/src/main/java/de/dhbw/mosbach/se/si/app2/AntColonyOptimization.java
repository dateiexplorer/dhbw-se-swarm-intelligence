package de.dhbw.mosbach.se.si.app2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.mosbach.se.si.app2.agents.Ant;
import de.dhbw.mosbach.se.si.app2.agents.Trail;
import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;
import de.dhbw.mosbach.se.si.util.random.RandomGenerator;

public class AntColonyOptimization {

    private static final Logger LOGGER = Logger.getLogger(AntColonyOptimization.class.getName());

    private final ParameterConfiguration paramConfig;

    private final ExecutorService executor;

    // Use a distanceMatrix to calculate distances between all nodes to speed
    // up calculations. This matrix is not mutual, and it should be safe to use
    // for parallel read operations.
    private final double[][] distanceMatrix;

    // The pheromoneMatrix stores the pheromones at the edges one node i to
    // another node j.
    // The values change each iteration. It should be safe to use for parallel
    // read operations if no parallel thread writes the pheromoneMatrix.
    private final double[][] pheromoneMatrix;

    // List of nodes. This list in not mutual.
    private final List<Node> nodes;

    // List of Callables that let an ant run and construct new solutions.
    private final List<Callable<Trail>> antSolutionConstructors;

    // Store the current best trail with the corresponding length
    private Trail bestTrail = null;
    private double bestTrailLength = Double.MAX_VALUE;

    public AntColonyOptimization(List<Node> nodes, ParameterConfiguration paramConfig) {
        this.nodes = nodes;
        this.paramConfig = paramConfig;

        LOGGER.log(Level.INFO, "Initialize ACO with " + Configuration.INSTANCE.threads + " threads");
        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        antSolutionConstructors = new ArrayList<>();

        distanceMatrix = generateDistanceMatrix(nodes);
        pheromoneMatrix = new double[nodes.size()][nodes.size()];
    }

    /**
     * Generates a 2-dimensional matrix which contains all distances to all
     * nodes.
     * 
     * @param nodes - the list of nodes for the TSP problem
     * @return Returns a 2-dimensional distance matrix.
     */
    private double[][] generateDistanceMatrix(List<Node> nodes) {
        LOGGER.log(Level.CONFIG, "Generate distance matrix");
        var numOfNodes = nodes.size();
        var distanceMatrix = new double[numOfNodes][numOfNodes];

        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    var from = nodes.get(i);
                    var to = nodes.get(j);
                    distanceMatrix[i][j] = from.distance(to, Configuration.INSTANCE.distanceFunc);
                }
            }
        }

        return distanceMatrix;
    }

    /**
     * Create ants based on the antsPerNode value and add them to the
     * antSolutionConstructors list.
     */
    private void setupAnts() {
        var numOfAnts = (int) (paramConfig.antsPerNode() * nodes.size());
        LOGGER.log(Level.CONFIG, "Setup " + numOfAnts + " ants to solve the TSP");

        antSolutionConstructors.clear();
        for (int i = 0; i < numOfAnts; i++) {
            // Every ant takes a reference to the pheromoneMatrix.
            // Because this is only a reference, the values are changing for each
            // ant and each iteration without further adjustments.
            antSolutionConstructors.add(
                    new Ant(i,this).getTrailWalker(pheromoneMatrix));
        }
    }

    /**
     * Construct a solution (Trail) for each ant. This method makes use of
     * multiple CPUs by let running the ants in parallel.
     * 
     * @return Returns the list of trails which are the solutions for this
     *         iteration.
     */
    private List<Trail> constructAntSolutionsParallel() {
        var trails = new ArrayList<Trail>();
        try {
            // Wait for all tasks to finish.
            // This is an implicit barrier and ensures that all things can be
            // done in a synchronized way if this method executes successfully.
            for (var future : executor.invokeAll(antSolutionConstructors)) {
                trails.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error while execute future tasks: " + e);
        }

        return trails;
    }

    /**
     * Initialize the pheromone matrix with the default pheromone value.
     */
    private void initPheromoneMatrix() {
        LOGGER.log(Level.CONFIG, "Initialize pheromone matrix with " + paramConfig.initialPheromoneValue());
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                pheromoneMatrix[i][j] = paramConfig.initialPheromoneValue();
            }
        }
    }

    /**
     * Update the pheromone matrix based on the contributions of the ants.
     * 
     * @param trails - the solutions generated by the ants for the current
     *               iteration
     */
    private void updatePheromoneMatrix(List<Trail> trails) {
        // Evaporate the values
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                pheromoneMatrix[i][j] *= (1.0 - paramConfig.evaporation());
            }
        }

        // Update the pheromones with the given method
        switch (paramConfig.pheromoneMatrixUpdateMethod()) {
            case BEST_TRAIL:
                updatePheromoneMatrixForTrail(getBestTrail(trails));
                break;
            case ALL_TRAILS:
            default:
                for (var trail : trails) {
                    updatePheromoneMatrixForTrail(trail);
                }
        }
    }

    /**
     * Update the pheromone matrix based on this trail. This is a helper
     * function fot the updatePheromoneMatrix.
     * 
     * @param trail - the trail represents a single solution for the TSP
     *              problem
     */
    private void updatePheromoneMatrixForTrail(Trail trail) {
        var contribution = paramConfig.q() / trail.length();
        LOGGER.log(Level.FINEST, "Add contribution for trail of ant " + trail.getAnt().getId() +
                " to pheromone matrix: " + contribution);
        for (int i = 0; i < nodes.size() - 1; i++) {
            pheromoneMatrix[trail.getNodeIndex(i)][trail.getNodeIndex(i + 1)] += contribution;
        }

        pheromoneMatrix[trail.getNodeIndex(nodes.size() - 1)][trail.getNodeIndex(0)] += contribution;
    }

    /**
     * Update the best global solution for the TSP problem.
     * 
     * @param trails - the solutions generated by the ants for the current
     *               iteration
     */
    private void updateBestTrail(List<Trail> trails) {
        var bestTrail = getBestTrail(trails);
        if (bestTrail.length() < bestTrailLength) {
            LOGGER.log(Level.INFO, "Update bestTrail with length " + bestTrail.length() + ": " +
                    bestTrail.toRoute(bestTrail.getAnt().getId()));
            this.bestTrail = bestTrail;
            this.bestTrailLength = bestTrail.length();
        }
    }

    /**
     * Helper function which calculates the divergence between the current best
     * solution and the average of the currently generated solutions.
     * 
     * A less divergence indicates that more ants chose the same (best) trail.
     * This value is useful for a termination condition.
     * If most of the ants chose the same (best) trail, the algorithm
     * convergent to a solution (which is near to the global optimum solution).
     * 
     * @param trails - the solutions generated by the ants for the current
     *               iteration
     * @return Returns the divergence of the current solutions to the current
     *         best solution.
     */
    private double calculateDivergence(List<Trail> trails) {
        var trailLengthCum = 0.0;
        for (var trail : trails) {
            trailLengthCum += trail.length();
        }

        // Calculate the average divergence between all length from the trails
        // of the current iteration and the current best trail.
        // A less value indicates that more ants found the best solution.
        return 1.0 - (this.bestTrailLength / (trailLengthCum / trails.size()));
    }

    /**
     * Helper function that returns the shortest trail from the given list of
     * trails.
     * 
     * @param trails - the list of trails to choose from
     * @return Returns the shortest trail from the given list of trails.
     */
    private Trail getBestTrail(List<Trail> trails) {
        Trail bestTrail = trails.get(0);
        var bestTrailLength = bestTrail.length();
        for (int i = 1; i < trails.size(); i++) {
            if (trails.get(i).length() < bestTrailLength) {
                bestTrail = trails.get(i);
                bestTrailLength = bestTrail.length();
            }
        }

        LOGGER.log(Level.FINER, "Best trail for this iteration with length " + bestTrailLength +
                " is from ant with id " + bestTrail.getAnt().getId() + ": " +
                bestTrail.toRoute(bestTrail.getAnt().getId()));
        return bestTrail;
    }

    /**
     * Runs the optimization algorithm and returns the best route.
     * 
     * @return Returns the best route for the TSP problem found by the ant
     *         colony optimization algorithm.
     */
    public Route run() {
        setupAnts();
        initPheromoneMatrix();

        for (int i = 0; i < paramConfig.maxIterations(); i++) {
            var trails = constructAntSolutionsParallel();
            updateBestTrail(trails);
            updatePheromoneMatrix(trails);

            // Terminate the algorithm, if the divergence of ant solutions is less than
            // the defined divergence.
            var divergence = calculateDivergence(trails);

            LOGGER.log(Level.FINE, "Iteration: " + i + ", Current best trail length: " + bestTrail.length() +
                    ", divergence: " + divergence);
            if (divergence < paramConfig.divergenceToTerminate()) {
                LOGGER.log(Level.INFO, "Break loop after " + i + " iterations because divergence of " +
                        paramConfig.divergenceToTerminate() + " was reached (divergence = " + divergence + ")");
                break;
            }
        }

        LOGGER.log(Level.INFO, "Algorithm ended because max iterations (" + paramConfig.maxIterations() + ")" +
                " where reached");
        executor.shutdownNow();
        return bestTrail.toRoute(0);
    }

    // Getter and setter

    public List<Node> getNodes() {
        return nodes;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public RandomGenerator getRandomGenerator() {
        return Configuration.INSTANCE.randomGenerator;
    }

    public double getAlpha() {
        return paramConfig.alpha();
    }

    public double getBeta() {
        return paramConfig.beta();
    }

    public double getRandomFactor() {
        return paramConfig.randomFactor();
    }
}
