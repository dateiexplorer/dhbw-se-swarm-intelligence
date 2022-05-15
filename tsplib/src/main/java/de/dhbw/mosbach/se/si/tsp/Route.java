package de.dhbw.mosbach.se.si.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;

public class Route {
    
    private final long id;
    private final List<City> cities = new ArrayList<>();

    public Route(long id, List<City> cities) {
        this.id = id;
        this.cities.addAll(cities);
    }

    public Route(long id, Route route) {
        this.id = id;
        this.cities.addAll(route.cities);
    }

    public double getTotalDistance(DistanceFunction func) {
        var size = cities.size();
        var totalDistance = 0D;

        // Cycle through cities, (i = size) == (i = 0)
        for (int i = 0; i < size; i++) {
            var current = cities.get(i);
            var next = cities.get((i + 1) % size);
            var distance = current.distance(next, func);
            totalDistance += distance;
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

    public long getId() {
        return id;
    }

    public List<City> getCities() {
        return cities;
    }
}