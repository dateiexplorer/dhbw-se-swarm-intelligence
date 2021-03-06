package de.dhbw.mosbach.se.si.app1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.mosbach.se.si.app1.permutators.PermutationIterator;
import de.dhbw.mosbach.se.si.app1.permutators.SequentialPermutationIterator;
import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;

public class BruteForce {

    private static final Logger LOGGER = Logger.getLogger(BruteForce.class.getName());

    // Setup for parallelization.
    private final ExecutorService executor;
    private final List<Future<?>> futures;

    private PermutationIterator permutationIterator;

    // Initial bestRouteLength and bestRoute.
    private Route bestRoute = null;
    private double bestRouteLength = Double.MAX_VALUE;

    // Kill switch for threads.
    private boolean kill = false;

    public BruteForce(List<Node> nodes) {
        LOGGER.log(Level.INFO, "Initialize brute-forcing with " + Configuration.INSTANCE.threads + " threads");
        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        futures = new ArrayList<>();
        
        // Iterator that get back all permutations of nodes.
        permutationIterator = new SequentialPermutationIterator(nodes);
    }

    private final Runnable searchBestRoute = () -> {
        LOGGER.log(Level.FINER, "Start runnable");
        Route bestRoute = null;
        var bestRouteLength = Double.MAX_VALUE;

        var route = permutationIterator.next();
        while (!kill && route != null) {
            var routeLength = route.getTotalDistance(Configuration.INSTANCE.distanceFunc);
            LOGGER.log(Level.FINER, "Calculate length for route (id = " + route.getId() + "): " + routeLength);
            
            // Calculate local optimum.
            if (routeLength < bestRouteLength) {
                LOGGER.log(Level.FINER, "Update local best route (old = [id: " +
                    (bestRoute != null ? bestRoute.getId() : "") + ", length: " + 
                    (bestRoute != null ? bestRouteLength : "") + "]): [id: " +
                    route.getId() + ", length: " + routeLength + "]");
                bestRouteLength = routeLength;
                bestRoute = route;
            }

            route = permutationIterator.next();
        }

        // Update global optimum. Synchronize for all threads.
        synchronized (this) {
            if (bestRouteLength < this.bestRouteLength) {
                assert bestRoute != null;
                LOGGER.log(Level.FINE, "Update global best route (old = [id: " +
                    (this.bestRoute != null ? this.bestRoute.getId() : "") + ", length: " + 
                    (this.bestRoute != null ? this.bestRouteLength : "") + "]): [id: " +
                    bestRoute.getId() + ", length: " + bestRouteLength + "]");
                this.bestRouteLength = bestRouteLength;
                this.bestRoute = bestRoute;
            }
        }
    };

    public Route run() {
        LOGGER.log(Level.INFO, "Start brute-forcing best route");
        for (int i = 0; i < Configuration.INSTANCE.threads; i++) {
            futures.add(executor.submit(searchBestRoute));
        }

        executor.shutdown();
        try {
            LOGGER.log(Level.INFO,
                    "Wait for termination (timeout = " + Configuration.INSTANCE.timeoutInMinutes + " min)");
            
            // executorTerminated is true if all threads end in a normal
            // way and the timeout isn't reached yet.
            var executorTerminated =
                    executor.awaitTermination(Configuration.INSTANCE.timeoutInMinutes, TimeUnit.MINUTES);
            
            // After timeout enable kill switch and shutdown all threads so
            // that they contribute their work to get a result (which is
            // normally not the best yet).
            kill = true;

            // Wait for tasks to finish.
            for (final var f : futures) {
                f.get();
            }

            if (executorTerminated) {
                LOGGER.log(Level.INFO, "Brute-forcing ended because all permutations were brute-forced");
            } else {
                LOGGER.log(Level.INFO, "Brute-forcing ended through timeout");
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error while await termination of the executor:" + e);
        }

        return bestRoute;
    }
}
