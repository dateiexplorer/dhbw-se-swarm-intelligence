package de.dhbw.mosbach.se.si.app2;

import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

public class App {
    
    public static void main(String[] args) {
        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(Configuration.INSTANCE.tspFile);
        System.out.println("Load TSP with " + nodes.size() + " nodes");

        var startTime = System.currentTimeMillis();

        // Run optimization process.
        var optimizer = new AntColonyOptimization(nodes);
        var bestRoute = optimizer.run();

        var deltaInSeconds = ((System.currentTimeMillis() - startTime) / 1000.0);

        // Log bestRoute.
        System.out.println("Best route found (length = " + 
            bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + "): " +
            bestRoute + " in " + deltaInSeconds + "s");
    }
}
