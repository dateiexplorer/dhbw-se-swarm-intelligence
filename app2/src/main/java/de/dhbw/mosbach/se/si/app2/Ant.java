package de.dhbw.mosbach.se.si.app2;

import java.util.List;

import de.dhbw.mosbach.se.si.tsp.City;

public class Ant {
    
    private final List<City> cities;
    private final double[][] distanceMatrix;

    private final int numOfCities;
    private final boolean[] visited;
    
    private Trail trail;

    public Ant(List<City> cities, double[][] distanceMatrix) {
        this.cities = cities;
        this.distanceMatrix = distanceMatrix;
        
        this.numOfCities = cities.size();
        visited = new boolean[numOfCities];
    }

    public void walkNewTrail(double[][] pheromoneMatrix) {
        clearVisitedInformation();
        var trail = new Trail(cities, distanceMatrix);

        // First add a random city.
        var cityIndex = Configuration.INSTANCE.randomGenerator.nextInt(numOfCities);
        trail.add(cityIndex);
        visited[cityIndex] = true;

        // Add new cities until all cities was visited by the ant.
        for (int i = 1; i < numOfCities; i++) {
            var currentCityIndex = trail.getCityIndex(i - 1);
            var nextCityIndex = selectNextCity(currentCityIndex, pheromoneMatrix);
            trail.add(nextCityIndex);
            visited[nextCityIndex] = true;
        }

        this.trail = trail;
    }

    private int selectNextCity(int currentCityIndex, double[][] pheromoneMatrix) {
        var randomNumber = Configuration.INSTANCE.randomGenerator.nextDouble();
        
        var probabilities = calculateProbabilities(currentCityIndex, pheromoneMatrix);
        // Cummulate probabilites (value between 0.0 and 1.0)
        var probabilitiesCum = 0.0;
        
        for (int i = 0; i < probabilities.length; i++) {
            probabilitiesCum += probabilities[i];
            if (probabilitiesCum >= randomNumber) {
                return i;
            }
        }

        throw new RuntimeException("failed to select next city (randomNumber = " +
            randomNumber + ", probabilitiesCum = " + probabilitiesCum + ")");
    }

    public double[] calculateProbabilities(int currentCityIndex, double[][] pheromoneMatrix) {
        var probabilities = new double[numOfCities];

        // Sum up probabilities to norm probabilities.
        var probabilitiesSum = 0.0;
        
        // Calculate probabilities for which position will be choose next from
        // the current position.
        for (int nextCityIndex = 0; nextCityIndex < numOfCities; nextCityIndex++) {
            // Visit cities only once.
            if (visited[nextCityIndex]) continue;

            // If a distance between two cities is less than or equal 0.0,
            // use 1.0 as the desirability factor.
            // This is important to avoid dividing with zero.
            // This implementation assumes that the distance between two cities
            // is normally greater than 1.0.
            var distance = distanceMatrix[currentCityIndex][nextCityIndex];
            var desirability = distance <= 0.0 ? 1.0 : 1.0 / distance;

            var pheromones = pheromoneMatrix[currentCityIndex][nextCityIndex];

            // Calculate probability for the city at index nextCityIndex.
            probabilities[nextCityIndex] =
                Math.pow(pheromones, Configuration.INSTANCE.alpha) *
                Math.pow(desirability, Configuration.INSTANCE.beta);
            
            // Sum up the probabilities
            probabilitiesSum += probabilities[nextCityIndex];
        }

        for (int i = 0; i < numOfCities; i++) {
            // If a position was already visited there is no chance to visit it
            // a second time.
            if (visited[i]) {
                probabilities[i] = 0.0;
            } else {
                probabilities[i] = probabilities[i] / probabilitiesSum;
            }
        }

        return probabilities;
    }

    public void clearVisitedInformation() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
    }

    public Trail getTrail() {
        return trail;
    }
}
