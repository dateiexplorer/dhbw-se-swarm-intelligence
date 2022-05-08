package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.DistanceFunc;
import de.dhbw.mosbach.se.si.util.distance.EuclideanDistance;

public enum Configuration {
    INSTANCE;

    final int threads = Runtime.getRuntime().availableProcessors();
    final int timeoutInMinutes = 1;

    final String tspFile = "octagon.tsp";
    final DistanceFunc distanceFunc = new EuclideanDistance();
}
