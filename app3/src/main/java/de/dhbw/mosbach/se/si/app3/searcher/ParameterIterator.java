package de.dhbw.mosbach.se.si.app3.searcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ParameterIterator<T extends Comparable<T>> implements Iterable<T> {

    private final List<T> values;

    public ParameterIterator(T min, T max, Function<T, T> stepFunction) {
        this.values = new ArrayList<>();

        T value = min;
        for (; value.compareTo(max) <= 0; value = stepFunction.apply(value)) {
            values.add(value);
        }
    }

    public int size() {
        return values.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            private int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < values.size();
            }

            @Override
            public T next() {
                return values.get(counter++);
            }
        };
    }
}
