package de.dhbw.mosbach.se.si.app2.agents;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.mosbach.se.si.app2.AntColonyOptimization;
import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.util.random.RandomGenerator;

public class Ant {

    private static final Logger LOGGER = Logger.getLogger(Ant.class.getName());

    private final long id;
    private final List<Node> nodes;
    private final double[][] distanceMatrix;

    private final RandomGenerator randomGenerator;

    private final int numOfNodes;
    private final boolean[] visited;

    private final double alpha;
    private final double beta;
    private final double randomFactor;

    public Ant(long id, AntColonyOptimization aco) {
        this.id = id;
        this.nodes = aco.getNodes();
        this.distanceMatrix = aco.getDistanceMatrix();

        // Clone the random generator from ACO for each ant with a new random
        // seed. The clone is necessary that ants can construct their solutions
        // in parallel. A new seed is needed that the ants walk other trails
        // and not use the same pseudo random numbers.
        this.randomGenerator = aco.getRandomGenerator().cloneWithSeed(System.nanoTime());

        this.alpha = aco.getAlpha();
        this.beta = aco.getBeta();
        this.randomFactor = aco.getRandomFactor();

        this.numOfNodes = nodes.size();
        visited = new boolean[numOfNodes];
    }

    public Callable<Trail> getTrailWalker(double[][] pheromoneMatrix) {
        return () -> walkNewTrail(pheromoneMatrix);
    }

    public Trail walkNewTrail(double[][] pheromoneMatrix) {
        clearVisitedInformation();
        var trail = new Trail(this, nodes, distanceMatrix);

        // First add a random node.
        var nodeIndex = randomGenerator.nextInt(numOfNodes);
        trail.add(nodeIndex);
        visited[nodeIndex] = true;

        // Add new nodes until all nodes was visited by the ant.
        for (int i = 1; i < numOfNodes; i++) {
            var currentNodeIndex = trail.getNodeIndex(i - 1);
            var nextNodeIndex = selectNextNode(currentNodeIndex, pheromoneMatrix);
            trail.add(nextNodeIndex);
            visited[nextNodeIndex] = true;
        }

        LOGGER.log(Level.FINEST, "New trail for ant " + id + ": " + trail.toRoute(id));
        return trail;
    }

    private int selectNextNode(int currentNodeIndex, double[][] pheromoneMatrix) {
        var randomNumber = randomGenerator.nextDouble();

        // Introduce more randomness
        if (randomNumber < randomFactor) {
            var randomNodeIndex = randomGenerator.nextInt(numOfNodes);
            if (!visited[randomNodeIndex]) {
                return randomNodeIndex;
            }
        }

        var probabilities = calculateProbabilities(currentNodeIndex, pheromoneMatrix);
        
        // Cumulate probabilities (value between 0.0 and 1.0)
        var probabilitiesCum = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            probabilitiesCum += probabilities[i];

            // Clip the probabilitiesCum to a max. value of 1.0, because of
            // false rounding of doubles to avoid exceptions, because
            // probabilitiesCum should be a value between 0.0 and 1.0
            probabilitiesCum = Math.min(probabilitiesCum, 1.0);
            if (probabilitiesCum >= randomNumber) {
                return i;
            }
        }

        throw new RuntimeException("failed to select next node (randomNumber = " +
                randomNumber + ", probabilitiesCum = " + probabilitiesCum + ")");
    }

    private double[] calculateProbabilities(int currentNodeIndex, double[][] pheromoneMatrix) {
        LOGGER.log(Level.FINEST, "Calculate new probabilities for ant " + id);
        var probabilities = new double[numOfNodes];

        // Sum up probabilities to norm probabilities.
        var probabilitiesSum = 0.0;

        // Calculate probabilities for which position will be chosen next from
        // the current position.
        for (int nextNodeIndex = 0; nextNodeIndex < numOfNodes; nextNodeIndex++) {
            // Visit nodes only once.
            if (visited[nextNodeIndex])
                continue;

            // If a distance between two nodes is less than or equal 0.0,
            // use 1.0 as the desirability factor.
            // This is important to avoid dividing with zero.
            // This implementation assumes that the distance between two nodes
            // is normally greater than 1.0.
            var distance = distanceMatrix[currentNodeIndex][nextNodeIndex];
            var desirability = distance <= 0.0 ? 1.0 : (1.0 / distance);

            var pheromones = pheromoneMatrix[currentNodeIndex][nextNodeIndex];

            // Calculate probability for the node at index nextNodeIndex.
            probabilities[nextNodeIndex] = 
                Math.pow(pheromones, alpha) * Math.pow(desirability, beta);

            // Sum up the probabilities
            probabilitiesSum += probabilities[nextNodeIndex];
        }

        for (int i = 0; i < numOfNodes; i++) {
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
        LOGGER.log(Level.FINEST, "Clear visited information of ant " + id);
        Arrays.fill(visited, false);
    }

    public long getId() {
        return id;
    }
}
