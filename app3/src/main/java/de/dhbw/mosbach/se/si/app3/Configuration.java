package de.dhbw.mosbach.se.si.app3;

import de.dhbw.mosbach.se.si.app2.parmeter.PheromoneMatrixUpdateMethod;
import de.dhbw.mosbach.se.si.app3.searcher.ParameterIterator;
import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    public final String tspFile = "a280.tsp";
    public final DistanceFunction distancefunction = new EuclideanDistance();

    public final ParameterIterator<Integer> maxIterationsIterator =
            new ParameterIterator<>(2000, 2000, (value) -> value + 1000);

    public final ParameterIterator<Double> antsPerNodeIterator =
            new ParameterIterator<>(0.6, 0.8, (value) -> value + 0.1);

    public final ParameterIterator<Double> initialPheromoneValueIterator =
            new ParameterIterator<>(1.0, 1.0, (value) -> value + 1.0);

    public final ParameterIterator<Double> evaporationIterator =
            new ParameterIterator<>(0.003, 0.004, (value) -> value + 0.001);

    public final ParameterIterator<Double> qIterator =
            new ParameterIterator<>(100.0, 100.0, (value) -> value + 100.0);

    public final ParameterIterator<Double> alphaIterator =
            new ParameterIterator<>(1.3, 1.4, (value) -> value + 0.1);

    public final ParameterIterator<Double> betaIterator =
            new ParameterIterator<>(1.9, 2.0, (value) -> value + 0.1);

    public final ParameterIterator<Double> randomFactorIterator =
            new ParameterIterator<>(0.0001, 0.0001, (value) -> value + 0.0001);

    public final ParameterIterator<Double> divergenceToTerminateIterator =
            new ParameterIterator<>(1e-3, 1e-3, (value) -> value + 1e-1);

    public final PheromoneMatrixUpdateMethod[] pheromoneMatrixUpdateMethodIterator =
            PheromoneMatrixUpdateMethod.values();
}
