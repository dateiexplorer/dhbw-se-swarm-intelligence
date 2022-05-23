package de.dhbw.mosbach.se.si.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;

public class Route {
    
    private final long id;
    private final List<Node> nodes = new ArrayList<>();

    public Route(long id, List<Node> nodes) {
        this.id = id;
        this.nodes.addAll(nodes);
    }

    public Route(long id, Route route) {
        this.id = id;
        this.nodes.addAll(route.nodes);
    }

    public double getTotalDistance(DistanceFunction func) {
        var size = nodes.size();
        var totalDistance = 0D;

        // Cycle through nodes, (i = size) == (i = 0)
        for (int i = 0; i < size; i++) {
            var current = nodes.get(i);
            var next = nodes.get((i + 1) % size);
            var distance = current.distance(next, func);
            totalDistance += distance;
        }

        return totalDistance;
    }

    @Override
    public String toString() {
        return "[" +
            // Separate node names by commas.
            nodes.stream()
                    .map(Node::getName)
                    .collect(Collectors.joining(", ")) +
            "]";
    }

    public long getId() {
        return id;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}