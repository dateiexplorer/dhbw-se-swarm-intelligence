package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    public final String loggingPropertiesFile = "logging.properties";

    public final int threads = Runtime.getRuntime().availableProcessors();
    
    public final DistanceFunction distanceFunc = new EuclideanDistance();

    public final String tspFile = "octagon.tsp";
    
    public final int timeoutInMinutes = 1;
}
