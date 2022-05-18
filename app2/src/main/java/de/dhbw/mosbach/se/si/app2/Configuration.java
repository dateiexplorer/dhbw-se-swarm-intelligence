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

    public final HashMap<String, String> paramStore = new HashMap<String, String>();

    public String tspFile = "a280.tsp";

    public int maxIterations = 3000;
    public double antsPerNode = 0.8;
    public double initialPheromonValue = 1.0;
    public double evaporation = 0.05;
    public double q = 100;
    public double alpha = 1.5;
    public double beta = 2;
    public double randomFactor = 0.0001;

    // Optional flags

    public final boolean USE_ONLY_BEST_TRAIL_FOR_PHEROMONE_UPDATE = false;
}
