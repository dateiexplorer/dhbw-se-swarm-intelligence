package de.dhbw.mosbach.se.si.util.distance;

import de.dhbw.mosbach.se.si.util.Vector2D;

public class EuclideanDistance implements DistanceFunction {
    
    @Override
    public double apply(Vector2D v1, Vector2D v2) {
        var h = v1.x() - v2.x();
        var v = v1.y() - v2.y();
        return Math.sqrt(h * h + v * v);
    }
}
