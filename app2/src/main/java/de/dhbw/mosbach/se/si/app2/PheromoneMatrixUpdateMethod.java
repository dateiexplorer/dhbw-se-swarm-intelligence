package de.dhbw.mosbach.se.si.app2;

public enum PheromoneMatrixUpdateMethod {

    /**
     * Use all trails to calculate the contributions to the pheromone matrix.
     */
    ALL_TRAILS,
    
    /**
     * Use only the best trail from the current iteration for the pheromone
     * contribution.
     */
    BEST_TRAIL;
}
