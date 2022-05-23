package de.dhbw.mosbach.se.si.app2;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.util.random.RandomGenerator;
import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;
import de.dhbw.mosbach.se.si.util.random.MersenneTwisterFast;

public enum Configuration {
    INSTANCE;

    public final int threads = Runtime.getRuntime().availableProcessors();

    public final DistanceFunction distanceFunc = new EuclideanDistance();
    public final RandomGenerator randomGenerator = new MersenneTwisterFast(System.nanoTime());

    // Configurable through command line arguments

    public final HashMap<String, String> paramStore = new HashMap<>();

    public String tspFile = "a280.tsp";

    public int maxIterations = 3000; // 3000
    public double antsPerNode = 0.8; // 0.8
    public double initialPheromoneValue = 1.0;
    public double evaporation = 0.006; // 0.006
    public double q = 100; // 100
    public double alpha = 1.35; // 1.35
    public double beta = 2.0; // 2.0
    public double randomFactor = 0.0001; // 0.0001

    public double divergenceToTerminate = 1e-3; // 1e-3

    public PheromoneMatrixUpdateMethod pheromoneMatrixUpdateMethod =
        PheromoneMatrixUpdateMethod.BEST_TRAIL; // BEST_TRAIL
}
