package de.dhbw.mosbach.se.si.util.loader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.dhbw.mosbach.se.si.tsp.Node;

public class NodeLoader {

    public List<Node> loadNodesFromFile(String file) {
        try (var inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(file)) {

            var data = inputStream.readAllBytes();
            return parseNodes(new String(data, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException(String.format("File '%s' couldn't be load properly", file));
    }

    private List<Node> parseNodes(String data) {
        // This regex matches all lines that have the following syntax:
        // <some_identifier> <x> <y>
        var pattern = Pattern.compile("^\\s*(\\S+)\\s+(\\d{1,})\\s+(\\d{1,})\\s*$");

        var nodes = new ArrayList<Node>();
        for (var line : data.lines().collect(Collectors.toList())) {
            var matcher = pattern.matcher(line);
            if (matcher.matches()) {
                nodes.add(new Node(matcher.group(1),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
                ));
            }
        }

        return nodes;
    }
}
