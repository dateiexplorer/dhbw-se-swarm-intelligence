package de.dhbw.mosbach.se.si.tsp;

import de.dhbw.mosbach.se.si.util.Vector2D;
import de.dhbw.mosbach.se.si.util.distance.DistanceFunction;

public class City {
    
    private final String name;
    private final Vector2D location;

    public City(String name, Vector2D location) {
        this.name = name;
        this.location = location;
    }

    public City(String name, double x, double y) {
        this.name = name;
        this.location = new Vector2D(x, y);
    }

    public double distance(City other, DistanceFunction func) {
        return func.apply(this.location, other.location);
    }

    public String getName() {
        return name;
    }

    public Vector2D getLocation() {
        return location;
    }
}