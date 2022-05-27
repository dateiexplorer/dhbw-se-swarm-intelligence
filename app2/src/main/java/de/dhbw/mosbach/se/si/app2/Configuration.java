package de.dhbw.mosbach.se.si.app2;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.app2.parmeter.PheromoneMatrixUpdateMethod;
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

    public final ParameterConfiguration defaultParamConfig =
            new ParameterConfiguration(
                    "a280.tsp",
                    3000,
                    0.8,
                    1.0,
                    0.006,
                    100,
                    1.35,
                    2.0,
                    0.0001,
                    1e-3,
                    PheromoneMatrixUpdateMethod.BEST_TRAIL
            );
}
