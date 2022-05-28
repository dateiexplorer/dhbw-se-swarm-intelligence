package de.dhbw.mosbach.se.si.app2;

import com.google.gson.Gson;
import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.tsp.Route;
import de.dhbw.mosbach.se.si.util.loader.NodeLoader;

import java.io.FileInputStream;
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
        var paramConfig = Configuration.INSTANCE.defaultParamConfig;

        // Check if command line arguments are complete.
        if (args.length % 2 != 0) {
            System.err.println("Command line arguments not sufficient.");
            System.exit(1);
        }

        // Get command line arguments.
        for (int i = 0; i < args.length; i += 2) {
            var param = args[i];
            var value = args[i + 1];
            if ("-best".equals(param)) {
                try (var reader = new FileInputStream(value)) {
                    var data = reader.readAllBytes();
                    System.out.println(new String(data));
                    paramConfig = new Gson()
                            .fromJson(new String(data), ParameterConfiguration.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.err.printf("Parameter %s is unknown.\n", param);
            }
        }

        var startTime = System.currentTimeMillis();

        // Run optimization process.
        var bestRoute = run(paramConfig);

        var deltaInSeconds = ((System.currentTimeMillis() - startTime) / 1000.0);

        // Log bestRoute.
        LOGGER.log(Level.INFO, "Best route found in " + deltaInSeconds + "s" + " with total length " +
                bestRoute.getTotalDistance(Configuration.INSTANCE.distanceFunc) + ": " + bestRoute);
    }

    public static Route run(ParameterConfiguration paramConfig) {
        // Load TSP data from file.
        var loader = new NodeLoader();
        var nodes = loader.loadNodesFromFile(paramConfig.tspFile());
        LOGGER.log(Level.INFO, "Load TSP problem with " + nodes.size() + " nodes");

        // Run optimization process.
        var optimizer = new AntColonyOptimization(nodes, paramConfig);

        return optimizer.run();
    }
}
