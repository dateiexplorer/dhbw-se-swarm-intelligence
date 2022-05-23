package de.dhbw.mosbach.se.si.app1;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    public final int threads = Runtime.getRuntime().availableProcessors();
    
    public final DistanceFunction distanceFunc = new EuclideanDistance();

    // Configurable through command line arguments

    public final HashMap<String, String> paramStore = new HashMap<>();
    
    public String tspFile = "octagon.tsp";
    
    public int timeoutInMinutes = 1;
}
