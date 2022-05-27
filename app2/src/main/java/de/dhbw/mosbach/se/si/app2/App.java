package de.dhbw.mosbach.se.si.app2;

import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.tsp.Route;
import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

public class App {
    
    public static void main(String[] args) {
        var startTime = System.currentTimeMillis();

        // Run optimization process.
        var bestRoute = run(Configuration.INSTANCE.defaultParamConfig);

        var deltaInSeconds = ((System.currentTimeMillis() - startTime) / 1000.0);

        // Log bestRoute.
        System.out.println("Best route found (length = " + 
            bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + "): " +
            bestRoute + " in " + deltaInSeconds + "s");
    }

    public static Route run(ParameterConfiguration paramConfig) {
        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(paramConfig.tspFile());
        System.out.println("Load TSP with " + nodes.size() + " nodes");

        // Run optimization process.
        var optimizer = new AntColonyOptimization(nodes, paramConfig);

        return optimizer.run();
    }
}
