package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

public class App {
    
    public static void main(String[] args) {
        System.out.println("Set timeout = " + Configuration.INSTANCE.timeoutInMinutes + "m");
        System.out.println("Set input = " + Configuration.INSTANCE.tspFile);

        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(Configuration.INSTANCE.tspFile);
        System.out.println("Load TSP with " + nodes.size() + " nodes");

        // Run optimization process.
        var optimizer = new BruteForce(nodes);
        var bestRoute = optimizer.run();

        // Log bestRoute.
        System.out.println("Best route found (length = " +
            bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + "): " +
            bestRoute);
    }
}
