package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.loader.Loader;

public class App {
    public static void main(String[] args) {
        // Load TSP data from file.
        var loader = new Loader();
        var cities = loader.loadCitiesFromFile(Configuration.INSTANCE.tspFile);
        System.out.println("Load TSP with " + cities.size() + " cities");

        // Run optimization process.
        var optimizer = new BruteForce(cities);
        var bestRoute = optimizer.run();

        // Log bestRoute.
        System.out.println("Best route found (fitness = " + 
            bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + "): " +
            bestRoute);
    }
}
