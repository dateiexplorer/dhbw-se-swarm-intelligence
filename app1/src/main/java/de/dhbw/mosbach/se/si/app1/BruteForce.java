package de.dhbw.mosbach.se.si.app1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.dhbw.mosbach.se.si.app1.permutators.PermutationIterator;
import de.dhbw.mosbach.se.si.app1.permutators.SequentialPermutationIterator;
import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;

public class BruteForce {
    
    private final ExecutorService executor;
    private final List<Future<?>> futures;
    private PermutationIterator permutator;

    // Initial bestRouteLength and bestRoute.
    private Route bestRoute = null;
    private double bestRouteLength = Double.MAX_VALUE;

    // Kill switch for threads.
    private boolean kill = false;

    public BruteForce(List<Node> nodes) {
        System.out.println("Initialize brute-forcing with " + Configuration.INSTANCE.threads + " threads");
        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        futures = new ArrayList<>();
        
        // Iterator that get back all permutations of nodes.
        permutator = new SequentialPermutationIterator(nodes);
    }

    private final Runnable searchBestRoute = () -> {
        Route bestRoute = null;
        var bestRouteLength = Double.MAX_VALUE;

        var route = permutator.next();
        while (!kill && route != null) {
            var routeLength = route.getTotalDistance(Configuration.INSTANCE.distanceFunc);
            System.out.println("Calculate length for route (id = " + route.getId() + "): " + routeLength);
            
            // Calculate local optimum.
            if (routeLength < bestRouteLength) {
                System.out.println("Update local best route (old = [id: " + 
                    (bestRoute != null ? bestRoute.getId() : "") + ", length: " + 
                    (bestRoute != null ? bestRouteLength : "") + "]): [id: " +
                    route.getId() + ", length: " + routeLength + "]");
                bestRouteLength = routeLength;
                bestRoute = route;
            }

            route = permutator.next();
        }

        // Update global optimum. Synchronize for all threads.
        synchronized (this) {
            if (bestRouteLength < this.bestRouteLength) {
                assert bestRoute != null;
                System.out.println("Update global best route (old = [id: " +
                    (this.bestRoute != null ? this.bestRoute.getId() : "") + ", length: " + 
                    (this.bestRoute != null ? this.bestRouteLength : "") + "]): [id: " +
                    bestRoute.getId() + ", length: " + bestRouteLength + "]");
                this.bestRouteLength = bestRouteLength;
                this.bestRoute = bestRoute;
            }
        }
    };

    public Route run() {
        System.out.println("Brute-forcing best route");
        for (int i = 0; i < Configuration.INSTANCE.threads; i++) {
            futures.add(executor.submit(searchBestRoute));
        }

        executor.shutdown();
        try {
            System.out.println("Wait for termination (timeout = " + Configuration.INSTANCE.timeoutInMinutes + " min)");
            
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
                System.out.println("Brute-forcing ended because all permutations were brute-forced");
            } else {
                System.out.println("Brute-forcing ended through timeout");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return bestRoute;
    }
}
