package de.dhbw.mosbach.se.si.util.loader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.dhbw.mosbach.se.si.tsp.City;

public class CityLoader {

    public List<City> loadCitiesFromFile(String file) {
        try (var inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(file)) {

            var data = inputStream.readAllBytes();
            return parseCities(new String(data, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException(String.format("File '%s' couldn't be load properly", file));
    }

    private List<City> parseCities(String data) {
        // This regex matches all lines that have the following syntax:
        // <some_identifier> <x> <y>
        var pattern = Pattern.compile("^\\s*(\\S+)\\s+(\\d{1,})\\s+(\\d{1,3})\\s*$");

        var cities = new ArrayList<City>();
        for (var line : data.lines().collect(Collectors.toList())) {
            var matcher = pattern.matcher(line);
            if (matcher.matches()) {
                cities.add(new City(matcher.group(1),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
                ));
            }
        }

        return cities;
    }
}
