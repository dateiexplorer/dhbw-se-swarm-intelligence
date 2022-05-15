package de.dhbw.mosbach.se.si.util.distance;

import de.dhbw.mosbach.se.si.util.Vector2D;

public interface DistanceFunction {
    
    double apply(Vector2D v1, Vector2D v2);
}
