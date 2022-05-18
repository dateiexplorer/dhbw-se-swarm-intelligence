package de.dhbw.mosbach.se.si.app2.agents;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;

public class Trail {

    private final List<Node> nodes;
    private final double[][] distanceMatrix;

    private final int[] trail;
    private final int numOfNodes;

    private int currentIndex = 0;

    public Trail(List<Node> nodes, double[][] distanceMatrix) {
        this.nodes = nodes;
        this.distanceMatrix = distanceMatrix;

        this.numOfNodes = nodes.size();
        this.trail = new int[numOfNodes];
    }

    public void add(int nodeIndex) {
        trail[currentIndex] = nodeIndex;
        currentIndex++;
    }

    public int getNodeIndex(int trailIndex) {
        return trail[trailIndex];
    }

    public double length() {
        // First calculate the length from the last to the first node.
        var length = distanceMatrix[trail[numOfNodes - 1]][trail[0]];
        // Calculate for all nodes from the first to the last.
        for (int i = 0; i < numOfNodes - 1; i++) {
            length += distanceMatrix[trail[i]][trail[i + 1]];
        }

        return length;
    }

    public Route toRoute(long routeId) {
        var nodes = new ArrayList<Node>();
        for (var nodeIndex : trail) {
            nodes.add(this.nodes.get(nodeIndex));
        }

        var route = new Route(routeId, nodes);
        return route;
    }
}
