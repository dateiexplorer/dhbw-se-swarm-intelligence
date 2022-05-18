package de.dhbw.mosbach.se.si.app2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.dhbw.mosbach.se.si.app2.ant.Ant;
import de.dhbw.mosbach.se.si.app2.ant.Trail;
import de.dhbw.mosbach.se.si.tsp.City;
import de.dhbw.mosbach.se.si.tsp.Route;
import de.dhbw.mosbach.se.si.util.random.RandomGenerator;

public class AntColonyOptimization {
    
    private final ExecutorService executor;

    // Use a distanceMatrix to calculate distances between all cities to speed
    // up calculations. This matrix is unmutual and it should be safe to use
    /// for parallel read operations.
    private final double[][] distanceMatrix;

    // The pheromoneMatrix stores the pheromones at the edges one city i to
    // another city j.
    // The values changes each iteration. It should be safe to use for parallel
    // read operations if no parallel thread writes the pheromoneMatrix.
    private final double[][] pheromoneMatrix;
    
    // List of cities. This list in unmutual.
    private final List<City> cities;

    // List of Callables that let an ant run and construct new solutions. 
    private final List<Callable<Trail>> antSolutionConstructors;

    // Store the current best trail with the corresponding length
    private Trail bestTrail = null;
    private double bestTrailLength = Double.MAX_VALUE;

    public AntColonyOptimization(List<City> cities) {
        this.cities = cities;

        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        antSolutionConstructors = new ArrayList<>();

        distanceMatrix = generateDistanceMatrix(cities);
        pheromoneMatrix = new double[cities.size()][cities.size()];
    }

    /**
     * Generates a 2-dimensional matrix wich contains all distances to all
     * cities.
     * 
     * @param cities - List of cities for the TSP problem.
     * @return Returns a 2-dimensional distance matrix.
     */
    private double[][] generateDistanceMatrix(List<City> cities) {
        var numOfCities = cities.size();
        var distanceMatrix = new double[numOfCities][numOfCities];
        
        for (int i = 0; i < numOfCities; i++) {
            for (int j = 0; j < numOfCities; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    var from = cities.get(i);
                    var to = cities.get(j);
                    distanceMatrix[i][j] =
                        from.distance(to, Configuration.INSTANCE.distanceFunc);
                }
            }
        }

        return distanceMatrix;
    }

    private void setupAnts() {
        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            antSolutionConstructors.add(
                new Ant(this).new Walker(pheromoneMatrix));
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
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.initialPheromonValue;
            }
        }
    }

    private void updatePheromoneMatrix(List<Trail> trails) {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
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
        for (int i = 0 ; i < cities.size() - 1; i++) {
            pheromoneMatrix[trail.getCityIndex(i)][trail.getCityIndex(i + 1)] += contribution;
        }

        pheromoneMatrix[trail.getCityIndex(cities.size() - 1)][trail.getCityIndex(0)] += contribution;
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
            //     var trailLength = trail.length();
            //     if (trailLength < bestTrailLength) {
            //         bestTrailLength = trailLength;
            //     }
            // }

            System.out.println(i + " > bestTrail.length(): " + bestTrail.length());
        }

        return bestTrail.toRoute(0);
    }

    public List<City> getCities() {
        return cities;
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
