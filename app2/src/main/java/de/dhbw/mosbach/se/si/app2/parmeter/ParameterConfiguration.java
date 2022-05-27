package de.dhbw.mosbach.se.si.app2.parmeter;

public record ParameterConfiguration(String tspFile, int maxIterations, double antsPerNode,
                                     double initialPheromoneValue, double evaporation, double q,
                                     double alpha, double beta, double randomFactor,
                                     double divergenceToTerminate,
                                     PheromoneMatrixUpdateMethod pheromoneMatrixUpdateMethod) {
}
