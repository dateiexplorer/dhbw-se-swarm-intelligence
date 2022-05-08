package de.dhbw.mosbach.se.si.app1.permutators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dhbw.mosbach.se.si.tsp.City;
import de.dhbw.mosbach.se.si.tsp.Route;

public class SequentialPermutator implements Permutator {

    private List<City> cities;
    private final int[] indexes;
    private final int size;
    
    private int currentIndex = -1;
    private long idCounter = 0;

    public SequentialPermutator(List<City> cities) {
        this.cities = new ArrayList<City>(cities);

        size = cities.size();
        indexes = new int[size];
    }

    // This function is heavily inspired by
    // https://www.baeldung.com/java-array-permutations
    private List<City> permute(List<City> cities) {
        if (currentIndex >= size) {
            return null;
        }

        if (indexes[currentIndex] < currentIndex) {
            swap(cities,
                currentIndex % 2 == 0 ? 0 : indexes[currentIndex],
                currentIndex);
            indexes[currentIndex]++;
            currentIndex = 0;
            return cities;
        } else {
            indexes[currentIndex] = 0;
            currentIndex++;
            return permute(cities);
        }
    }

    private void swap(List<City> list, int a, int b) {
        Collections.swap(list, a, b);
    }

    // Generate new permutations if they are needed.
    @Override
    public synchronized Route next() {
        // Get first time the list unmodified.
        if (currentIndex == -1) {
            currentIndex++;
            return new Route(idCounter++, cities);
        }

        // Get permuted list.
        var permutedCities = permute(cities);
        if (permutedCities != null) {
            return new Route(idCounter++, permutedCities);
        }        

        return null;
    }
    
}
