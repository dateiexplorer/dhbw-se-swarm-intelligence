package de.dhbw.mosbach.se.si.app1;

import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    static {
        // Setup logging.
        try (var stream = App.class
                .getClassLoader()
                .getResourceAsStream(Configuration.INSTANCE.loggingPropertiesFile)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(Configuration.INSTANCE.tspFile);
        LOGGER.log(Level.INFO, "Load TSP problem with" + nodes.size() + "nodes");

        // Run optimization process.
        var optimizer = new BruteForce(nodes);
        var bestRoute = optimizer.run();

        // Log bestRoute.
        LOGGER.log(Level.INFO, "Best route found with total length of " +
                bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + ": " +
                bestRoute);
    }
}
