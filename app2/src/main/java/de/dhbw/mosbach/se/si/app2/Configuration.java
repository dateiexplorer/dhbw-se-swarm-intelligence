package de.dhbw.mosbach.se.si.app2;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.util.random.RandomGenerator;
import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;
import de.dhbw.mosbach.se.si.util.random.MersenneTwisterFast;

public enum Configuration {
    INSTANCE;

    final HashMap<String, String> paramStore = new HashMap<String, String>();

    // Defaults
    
    int threads = Runtime.getRuntime().availableProcessors();
    int maxIterations = 500;

    String tspFile = "a280.tsp";
    DistanceFunction distanceFunc = new EuclideanDistance();

    double initialPheromonValue = 1.0;
    double evaporation = 0.1;
    double q = 500;
    double alpha = 2;
    double beta = 2;

    int numberOfAnts = 280;

    final RandomGenerator randomGenerator = new MersenneTwisterFast(System.nanoTime());
}
