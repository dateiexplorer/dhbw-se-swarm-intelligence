package de.dhbw.mosbach.se.si.app3.searcher;

import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.app3.App;
import de.dhbw.mosbach.se.si.app3.Configuration;
import de.dhbw.mosbach.se.si.tsp.Route;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AntColonyParameterSearcher {

    private static final Logger LOGGER = Logger.getLogger(AntColonyParameterSearcher.class.getName());

    // Shorten variables a little bit.
    private final Configuration c = Configuration.INSTANCE;

    // Number of configurations to go through.
    private final int totalNumOfConfigurations;

    // Counts the configurations which already passed.
    int counter = 0;

    double deltaInSeconds = Double.POSITIVE_INFINITY;


    public AntColonyParameterSearcher() {
        totalNumOfConfigurations = c.maxIterationsIterator.size() *
                c.antsPerNodeIterator.size() *
                c.initialPheromoneValueIterator.size() *
                c.evaporationIterator.size() *
                c.qIterator.size() *
                c.alphaIterator.size() *
                c.betaIterator.size() *
                c.randomFactorIterator.size() *
                c.divergenceToTerminateIterator.size() *
                c.pheromoneMatrixUpdateMethodIterator.length;
    }

    public ParameterConfiguration run() {
        ParameterConfiguration bestParamConfig = null;
        double bestRouteLength = Double.MAX_VALUE;

        for (var pheromoneMatrixUpdateMethod : c.pheromoneMatrixUpdateMethodIterator) {
            for (var maxIterations : c.maxIterationsIterator) {
                for (var antsPerNode : c.antsPerNodeIterator) {
                    for (var initialPheromoneValue : c.initialPheromoneValueIterator) {
                        for (var evaporation : c.evaporationIterator) {
                            for (var q : c.qIterator) {
                                for (var alpha : c.alphaIterator) {
                                    for (var beta : c.betaIterator) {
                                        for (var randomFactor : c.randomFactorIterator) {
                                            for (var divergenceToTerminate : c.divergenceToTerminateIterator) {
                                                var paramConfig = new ParameterConfiguration(
                                                        c.tspFile,
                                                        maxIterations,
                                                        antsPerNode,
                                                        initialPheromoneValue,
                                                        evaporation,
                                                        q,
                                                        alpha,
                                                        beta,
                                                        randomFactor,
                                                        divergenceToTerminate,
                                                        pheromoneMatrixUpdateMethod
                                                );

                                                var route = testParameterConfiguration(paramConfig);

                                                var routeLength = route.getTotalDistance(c.distancefunction);
                                                if (routeLength < bestRouteLength) {
                                                    LOGGER.log(Level.INFO,
                                                            "New best configuration found: " + paramConfig);
                                                    bestParamConfig = paramConfig;
                                                    bestRouteLength = routeLength;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return bestParamConfig;
    }

    private Route testParameterConfiguration(ParameterConfiguration paramConfig) {
        counter++;

        var estimatedTimeRemaining = deltaInSeconds == Double.POSITIVE_INFINITY ?
                "Unknown" : String.valueOf((deltaInSeconds * (totalNumOfConfigurations - counter)) / 3600);
        LOGGER.log(Level.INFO, "Test configuration " + counter + "/" + totalNumOfConfigurations + ", " +
                "Estimated time remaining (in h): " + estimatedTimeRemaining);

        var startTime = System.currentTimeMillis();

        // Test the current paramConfig.
        var route = de.dhbw.mosbach.se.si.app2.App.run(paramConfig);

        deltaInSeconds = ((System.currentTimeMillis() - startTime) / 1000.0);
        return route;
    }
}
