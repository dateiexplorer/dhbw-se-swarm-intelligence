package de.dhbw.mosbach.se.si.app2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.dhbw.mosbach.se.si.tsp.City;
import de.dhbw.mosbach.se.si.tsp.Route;

public class AntColonyOptimization {
    
    private final ExecutorService executor;

    // Use a distantMatrix to calculate distances between all cities to speed
    // up calculations. This matrix is unmutual and it should be safe to use
    // for parallel read operations.
    private final double[][] distanceMatrix;

    private final double[][] pheromoneMatrix;
    private final List<Ant> ants;

    private List<City> cities;
    private Trail bestTrail = null;
    private double bestTrailLength = Double.MAX_VALUE;

    public AntColonyOptimization(List<City> cities) {
        this.cities = cities;
        distanceMatrix = generateDistanceMatrix(cities);

        pheromoneMatrix = new double[cities.size()][cities.size()];
        ants = new ArrayList<>();

        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
    }

    private double[][] generateDistanceMatrix(List<City> cities) {
        var numOfCities = cities.size();
        var distanceMatrix = new double[numOfCities][numOfCities];
        
        for (int i = 0; i < numOfCities; i++) {
            for (int j = 0; j < numOfCities; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    var fromCity = cities.get(i);
                    var toCity = cities.get(j);
                    distanceMatrix[i][j] =
                        fromCity.distance(toCity, Configuration.INSTANCE.distanceFunc);
                }
            }
        }

        return distanceMatrix;
    }

    private void setupAnts() {
        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            ants.add(new Ant(cities, distanceMatrix));
        }
    }

    private void constructAntSolutions() {
        for (var ant : ants) {
            ant.walkNewTrail(pheromoneMatrix);
        }
    }

    private void initPheromonMatrix() {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.initialPheromonValue;
            }
        }
    }

    private void updatePheromonMatrix() {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                pheromoneMatrix[i][j] *= (1.0 - Configuration.INSTANCE.evaporation);
            }
        }

        for (var ant : ants) {
            var contribution = Configuration.INSTANCE.q / ant.getTrail().length();

            var trail = ant.getTrail();
            for (int i = 0 ; i < cities.size() - 1; i++) {
                pheromoneMatrix[trail.getCityIndex(i)][trail.getCityIndex(i + 1)] += contribution;
            }

            pheromoneMatrix[trail.getCityIndex(cities.size() - 1)][trail.getCityIndex(0)] += contribution;
        }
    }

    private void updateBestTrail() {
        for (var ant : ants) {
            if (ant.getTrail().length() < bestTrailLength) {
                bestTrail = ant.getTrail();
                bestTrailLength = ant.getTrail().length();
            }
        }
    }

    public Route run() {
        setupAnts();       
        initPheromonMatrix();

        for (int i = 0; i < Configuration.INSTANCE.maxIterations; i++) {
            constructAntSolutions();
            updatePheromonMatrix();
            updateBestTrail();

            var bestRoute = this.bestTrail.toRoute(0);
            System.out.println(bestRoute + " (" + bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + ")");
        }

        return bestTrail.toRoute(0);
    }
}
