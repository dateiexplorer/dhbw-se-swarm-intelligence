package de.dhbw.mosbach.se.si.app2.ant;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.mosbach.se.si.tsp.City;
import de.dhbw.mosbach.se.si.tsp.Route;

public class Trail {

    private final List<City> cities;
    private final double[][] distanceMatrix;

    private final int[] trail;
    private final int numOfCities;

    private int currentIndex = 0;

    public Trail(List<City> cities, double[][] distanceMatrix) {
        this.cities = cities;
        this.distanceMatrix = distanceMatrix;

        this.numOfCities = cities.size();
        this.trail = new int[numOfCities];
    }

    public void add(int cityIndex) {
        trail[currentIndex] = cityIndex;
        currentIndex++;
    }

    public int getCityIndex(int trailIndex) {
        return trail[trailIndex];
    }

    public double length() {
        // First calculate the length from the last to the first city.
        var length = distanceMatrix[trail[numOfCities - 1]][trail[0]];
        // Calculate for all citites from the first to the last.
        for (int i = 0; i < numOfCities - 1; i++) {
            length += distanceMatrix[trail[i]][trail[i + 1]];
        }

        return length;
    }

    public Route toRoute(long routeId) {
        var cities = new ArrayList<City>();
        for (var cityIndex : trail) {
            cities.add(this.cities.get(cityIndex));
        }

        var route = new Route(routeId, cities);
        return route;
    }
}
