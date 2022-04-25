package de.dhbw.mosbach.se.si.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.mosbach.se.si.util.DistanceFunc;

public class Route {
    private final List<City> cities = new ArrayList<>();

    public Route(List<City> cities) {
        this.cities.addAll(cities);
    }

    public Route(Route route) {
        cities.addAll(route.cities);
    }

    public Route shuffled() {
        var shuffled = new ArrayList<City>(cities);
        Collections.shuffle(shuffled);
        return new Route(shuffled);
    }

    public double getTotalDistance(DistanceFunc func) {
        var size = cities.size();
        var totalDistance = 0D;

        // Cycle through cities, (i = size) == (i = 0)
        for (int i = 0; i <= size; i++) {
            var current = cities.get(i);
            var next = cities.get((i + 1) % size);
            totalDistance += current.distance(next, func);
        }

        return totalDistance;
    }

    @Override
    public String toString() {
        var s = new StringBuilder();
        s.append("[");

        // Separate city names by commas.
        s.append(cities.stream()
            .map(City::getName)
            .collect(Collectors.joining(", ")));

        s.append("]");
        return s.toString();
    }

    public List<City> getCities() {
        return cities;
    }
}