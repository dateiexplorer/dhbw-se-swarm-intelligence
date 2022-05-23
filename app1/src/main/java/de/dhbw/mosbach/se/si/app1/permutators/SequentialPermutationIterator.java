package de.dhbw.mosbach.se.si.app1.permutators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dhbw.mosbach.se.si.tsp.Node;
import de.dhbw.mosbach.se.si.tsp.Route;

public class SequentialPermutationIterator implements PermutationIterator {

    private final List<Node> nodes;
    private final int[] indexes;
    private final int size;
    
    private int currentIndex = -1;
    private long idCounter = 0;

    public SequentialPermutationIterator(List<Node> nodes) {
        this.nodes = new ArrayList<>(nodes);

        size = nodes.size();
        indexes = new int[size];
    }

    // This function is heavily inspired by
    // https://www.baeldung.com/java-array-permutations
    private List<Node> permute(List<Node> nodes) {
        if (currentIndex >= size) {
            return null;
        }

        if (indexes[currentIndex] < currentIndex) {
            swap(nodes,
                currentIndex % 2 == 0 ? 0 : indexes[currentIndex],
                currentIndex);
            indexes[currentIndex]++;
            currentIndex = 0;
            return nodes;
        } else {
            indexes[currentIndex] = 0;
            currentIndex++;
            return permute(nodes);
        }
    }

    private void swap(List<Node> list, int a, int b) {
        Collections.swap(list, a, b);
    }

    // Generate new permutations if they are needed.
    @Override
    public synchronized Route next() {
        // Get first time the list unmodified.
        if (currentIndex == -1) {
            currentIndex++;
            return new Route(idCounter++, nodes);
        }

        // Get permuted list.
        var permutedNodes = permute(nodes);
        if (permutedNodes != null) {
            return new Route(idCounter++, permutedNodes);
        }        

        return null;
    }
    
}
