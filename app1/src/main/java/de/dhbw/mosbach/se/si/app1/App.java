package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

public class App {
    
    public static void main(String[] args) {
        // Initialize variables with optional command line parameters.
        for (int i = 0; i < args.length; i++) {
            var param = args[i].split("=");
            if (param.length == 2) {
                var key = param[0].trim();
                var value = param[1].trim();
                Configuration.INSTANCE.paramStore.put(key, value);
            } else {
                System.out.println("no value found for argument '" + param[0] + "'");
                System.exit(1);
            }
        }
        
        // Load configurations form the paramStore.
        var tspFile = Configuration.INSTANCE.paramStore.get("input");
        if (tspFile != null) {
            Configuration.INSTANCE.tspFile = tspFile;
        }
        var timeoutInMinutes = Configuration.INSTANCE.paramStore.get("timeout");
        if (timeoutInMinutes != null) {
            try {
                var t = Integer.parseInt(timeoutInMinutes);
                Configuration.INSTANCE.timeoutInMinutes = t;
            } catch (NumberFormatException e) {
                System.out.println("timeout t is not a valid number.");
                System.exit(1);
            }
        }

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
