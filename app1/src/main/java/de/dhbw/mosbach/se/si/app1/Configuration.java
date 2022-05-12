package de.dhbw.mosbach.se.si.app1;

import java.util.HashMap;

import de.dhbw.mosbach.se.si.util.DistanceFunc;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    final HashMap<String, String> paramStore = new HashMap<String, String>();

    // Defaults
    int threads = Runtime.getRuntime().availableProcessors();
    int timeoutInMinutes = 1;

    String tspFile = "octagon.tsp";
    DistanceFunc distanceFunc = new EuclideanDistance();
}
