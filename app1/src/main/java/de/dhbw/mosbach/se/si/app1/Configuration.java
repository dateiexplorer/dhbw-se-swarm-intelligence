package de.dhbw.mosbach.se.si.app1;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    final HashMap<String, String> paramStore = new HashMap<String, String>();

    // Defaults

    int threads = Runtime.getRuntime().availableProcessors();
    int timeoutInMinutes = 1;

    String tspFile = "octagon.tsp";
    DistanceFunction distanceFunc = new EuclideanDistance();
}
