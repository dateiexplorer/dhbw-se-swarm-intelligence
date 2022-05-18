package de.dhbw.mosbach.se.si.app2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.dhbw.mosbach.se.si.app2.agents.Ant;
import de.dhbw.mosbach.se.si.app2.agents.Trail;
import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;
import de.dhbw.mosbach.se.si.util.random.RandomGenerator;

public class AntColonyOptimization {

    private final ExecutorService executor;

    // Use a distanceMatrix to calculate distances between all nodes to speed
    // up calculations. This matrix is unmutual and it should be safe to use
    /// for parallel read operations.
    private final double[][] distanceMatrix;

    // The pheromoneMatrix stores the pheromones at the edges one node i to
    // another node j.
    // The values changes each iteration. It should be safe to use for parallel
    // read operations if no parallel thread writes the pheromoneMatrix.
    private final double[][] pheromoneMatrix;

    // List of nodes. This list in unmutual.
    private final List<Node> nodes;

    // List of Callables that let an ant run and construct new solutions.
    private final List<Callable<Trail>> antSolutionConstructors;

    // Store the current best trail with the corresponding length
    private Trail bestTrail = null;
    private double bestTrailLength = Double.MAX_VALUE;

    public AntColonyOptimization(List<Node> nodes) {
        this.nodes = nodes;

        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        antSolutionConstructors = new ArrayList<>();

        distanceMatrix = generateDistanceMatrix(nodes);
        pheromoneMatrix = new double[nodes.size()][nodes.size()];
    }

    /**
     * Generates a 2-dimensional matrix wich contains all distances to all
     * nodes.
     * 
     * @param nodes - List of nodes for the TSP problem
     * @return Returns a 2-dimensional distance matrix.
     */
    private double[][] generateDistanceMatrix(List<Node> nodes) {
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

    private void setupAnts() {
        var numOfAnts = (int) Configuration.INSTANCE.antsPerNode * nodes.size();
        for (int i = 0; i < numOfAnts; i++) {
            antSolutionConstructors.add(
                    new Ant(this).getTrailWalker(pheromoneMatrix));
        }
    }

    private List<Trail> constructAntSolutionsParallel() {
        var trails = new ArrayList<Trail>();
        try {
            for (var future : executor.invokeAll(antSolutionConstructors)) {
                trails.add(future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return trails;
    }

    private void initPheromonMatrix() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.initialPheromonValue;
            }
        }
    }

    private void updatePheromoneMatrix(List<Trail> trails) {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                pheromoneMatrix[i][j] *= (1.0 - Configuration.INSTANCE.evaporation);
            }
        }

        if (Configuration.INSTANCE.USE_ONLY_BEST_TRAIL_FOR_PHEROMONE_UPDATE) {
            updatePheromoneMatrixForTrail(getBestTrail(trails));
            return;
        }

        for (var trail : trails) {
            updatePheromoneMatrixForTrail(trail);
        }
    }

    private void updatePheromoneMatrixForTrail(Trail trail) {
        var contribution = Configuration.INSTANCE.q / trail.length();
        for (int i = 0; i < nodes.size() - 1; i++) {
            pheromoneMatrix[trail.getNodeIndex(i)][trail.getNodeIndex(i + 1)] += contribution;
        }

        pheromoneMatrix[trail.getNodeIndex(nodes.size() - 1)][trail.getNodeIndex(0)] += contribution;
    }

    private void updateBestTrail(List<Trail> trails) {
        var bestTrail = getBestTrail(trails);
        if (bestTrail.length() < bestTrailLength) {
            this.bestTrail = bestTrail;
            this.bestTrailLength = bestTrail.length();
        }
    }

    private Trail getBestTrail(List<Trail> trails) {
        Trail bestTrail = null;
        var bestTrailLength = Double.MAX_VALUE;
        for (var trail : trails) {
            if (trail.length() < bestTrailLength) {
                bestTrail = trail;
                bestTrailLength = trail.length();
            }
        }

        return bestTrail;
    }

    public Route run() {
        setupAnts();
        initPheromonMatrix();

        for (int i = 0; i < Configuration.INSTANCE.maxIterations; i++) {
            var trails = constructAntSolutionsParallel();
            updateBestTrail(trails);
            updatePheromoneMatrix(trails);

            // var bestTrailLength = Double.MAX_VALUE;
            // for (var trail : trails) {
            // var trailLength = trail.length();
            // if (trailLength < bestTrailLength) {
            // bestTrailLength = trailLength;
            // }
            // }

            System.out.println(i + " > bestTrail.length(): " + bestTrail.length());
        }

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
        return Configuration.INSTANCE.alpha;
    }

    public double getBeta() {
        return Configuration.INSTANCE.beta;
    }

    public double getRandomFactor() {
        return Configuration.INSTANCE.randomFactor;
    }
}
