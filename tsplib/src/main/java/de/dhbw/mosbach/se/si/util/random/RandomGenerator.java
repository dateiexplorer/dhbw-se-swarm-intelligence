package de.dhbw.mosbach.se.si.util.random;

public interface RandomGenerator {
    
    RandomGenerator cloneWithSeed(long seed);

    double nextDouble();
    int nextInt(int n);
}
