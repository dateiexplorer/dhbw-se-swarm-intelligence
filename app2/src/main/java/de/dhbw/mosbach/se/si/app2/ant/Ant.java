package de.dhbw.mosbach.se.si.app2.ant;

import java.util.List;
import java.util.concurrent.Callable;

import de.dhbw.mosbach.se.si.app2.AntColonyOptimization;
import de.dhbw.mosbach.se.si.tsp.City;
import de.dhbw.mosbach.se.si.util.random.RandomGenerator;

public class Ant {
    
    private final Ant self;

    private final List<City> cities;
    private final double[][] distanceMatrix;

    private final int numOfCities;
    private final boolean[] visited;

    private final RandomGenerator randomGenerator;

    private final double alpha;
    private final double beta;

    private final double randomFactor;
    
    public Ant(AntColonyOptimization aco) {
        this.cities = aco.getCities();
        this.distanceMatrix = aco.getDistanceMatrix();

        this.randomGenerator = aco.getRandomGenerator().cloneWithSeed(System.nanoTime());

        this.alpha = aco.getAlpha();
        this.beta = aco.getBeta();
        this.randomFactor = aco.getRandomFactor();

        this.numOfCities = cities.size();
        visited = new boolean[numOfCities];

        self = this;
    }

    public class Walker implements Callable<Trail> {

        private final double[][] pheromoneMatrix;

        public Walker(double[][] pheromoneMatrix) {
            this.pheromoneMatrix = pheromoneMatrix;
        }

        @Override
        public Trail call() throws Exception {
            return self.walkNewTrail(pheromoneMatrix);
        }

    }

    private Trail walkNewTrail(double[][] pheromoneMatrix) {
        clearVisitedInformation();
        var trail = new Trail(cities, distanceMatrix);

        // First add a random city.
        var cityIndex = randomGenerator.nextInt(numOfCities);
        trail.add(cityIndex);
        visited[cityIndex] = true;

        // Add new cities until all cities was visited by the ant.
        for (int i = 1; i < numOfCities; i++) {
            var currentCityIndex = trail.getCityIndex(i - 1);
            var nextCityIndex = selectNextCity(currentCityIndex, pheromoneMatrix);
            trail.add(nextCityIndex);
            visited[nextCityIndex] = true;
        }

        return trail;
    }

    private int selectNextCity(int currentCityIndex, double[][] pheromoneMatrix) {
        var randomNumber = randomGenerator.nextDouble();
        
        // Introduce more randomness
        if (randomNumber < randomFactor) {
            var randomCityIndex = randomGenerator.nextInt(numOfCities);
            if (!visited[randomCityIndex]) {
                return randomCityIndex;
            }
        }
 
        var probabilities = calculateProbabilities(currentCityIndex, pheromoneMatrix);
        // Cummulate probabilites (value between 0.0 and 1.0)
        var probabilitiesCum = 0.0;
        
        for (int i = 0; i < probabilities.length; i++) {
            probabilitiesCum += probabilities[i];

            // Clip the probabilitiesCum to a max. value of 1.0, because of
            // unsharp rounding of doubles to avoid exceptions, because
            // probabilitiesCum should be a value between 0.0 and 1.0
            probabilitiesCum = probabilitiesCum > 1.0 ? 1.0 : probabilitiesCum;
            if (probabilitiesCum >= randomNumber) {
                return i;
            }
        }

        throw new RuntimeException("failed to select next city (randomNumber = " +
            randomNumber + ", probabilitiesCum = " + probabilitiesCum + ")");
    }

    private double[] calculateProbabilities(int currentCityIndex, double[][] pheromoneMatrix) {
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
            var desirability = distance <= 0.0 ? 1.0 : (1.0 / distance);

            var pheromones = pheromoneMatrix[currentCityIndex][nextCityIndex];

            // Calculate probability for the city at index nextCityIndex.
            probabilities[nextCityIndex] = Math.pow(pheromones, alpha) * Math.pow(desirability, beta);
            
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

    private void clearVisitedInformation() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
    }
}
