package de.dhbw.mosbach.se.si.app2;

import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

public class App {
    
    public static void main(String[] args) {
        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(Configuration.INSTANCE.tspFile);
        System.out.println("Load TSP with " + nodes.size() + " nodes");

        // Run optimization process.
        var optimizer = new AntColonyOptimization(nodes);
        var bestRoute = optimizer.run();

        // Log bestRoute.
        System.out.println("Best route found (length = " + 
            bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + "): " +
            bestRoute);
    }
}
