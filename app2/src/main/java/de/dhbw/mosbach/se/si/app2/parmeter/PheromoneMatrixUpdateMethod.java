package de.dhbw.mosbach.se.si.app2.parmeter;

import com.google.gson.annotations.SerializedName;

public enum PheromoneMatrixUpdateMethod {

    /**
     * Use all trails to calculate the contributions to the pheromone matrix.
     */
    @SerializedName("ALL_TRAILS")
    ALL_TRAILS,
    
    /**
     * Use only the best trail from the current iteration for the pheromone
     * contribution.
     */
    @SerializedName("BEST_TRAIL")
    BEST_TRAIL
}
