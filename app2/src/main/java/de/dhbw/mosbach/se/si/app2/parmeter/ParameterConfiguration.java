package de.dhbw.mosbach.se.si.app2.parmeter;

// This cannot be a record, because Gson cannot get variables.
@SuppressWarnings("ClassCanBeRecord")
public class ParameterConfiguration {

    private final String tspFile;
    private final int maxIterations;
    private final double antsPerNode;
    private final double initialPheromoneValue;
    private final double evaporation;
    private final double q;
    private final double alpha;
    private final double beta;
    private final double randomFactor;
    private final double divergenceToTerminate;
    private final PheromoneMatrixUpdateMethod pheromoneMatrixUpdateMethod;

    public ParameterConfiguration(
            String tspFile, int maxIterations, double antsPerNode,
            double initialPheromoneValue, double evaporation, double q,
            double alpha, double beta, double randomFactor,
            double divergenceToTerminate,
            PheromoneMatrixUpdateMethod pheromoneMatrixUpdateMethod) {
        this.tspFile = tspFile;
        this.maxIterations = maxIterations;
        this.antsPerNode = antsPerNode;
        this.initialPheromoneValue = initialPheromoneValue;
        this.evaporation = evaporation;
        this.q = q;
        this.alpha = alpha;
        this.beta = beta;
        this.randomFactor = randomFactor;
        this.divergenceToTerminate = divergenceToTerminate;
        this.pheromoneMatrixUpdateMethod = pheromoneMatrixUpdateMethod;
    }

    public String tspFile() {
        return tspFile;
    }

    public int maxIterations() {
        return maxIterations;
    }

    public double antsPerNode() {
        return antsPerNode;
    }

    public double initialPheromoneValue() {
        return initialPheromoneValue;
    }

    public double evaporation() {
        return evaporation;
    }

    public double q() {
        return q;
    }

    public double alpha() {
        return alpha;
    }

    public double beta() {
        return beta;
    }

    public double randomFactor() {
        return randomFactor;
    }

    public double divergenceToTerminate() {
        return divergenceToTerminate;
    }

    public PheromoneMatrixUpdateMethod pheromoneMatrixUpdateMethod() {
        return pheromoneMatrixUpdateMethod;
    }
}
