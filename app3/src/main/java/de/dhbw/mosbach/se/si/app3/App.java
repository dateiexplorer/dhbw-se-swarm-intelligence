package de.dhbw.mosbach.se.si.app3;

import com.google.gson.GsonBuilder;
import de.dhbw.mosbach.se.si.app3.searcher.AntColonyParameterSearcher;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class App {
    
    public static void main(String[] args) {
        var bestParamConfig = new AntColonyParameterSearcher().run();

        var rootDirectory = new File("data");
        if (!rootDirectory.mkdirs()) {
            throw new RuntimeException("Directories cannot be created.");
        }

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm");
        var file = new File(rootDirectory.getPath() +
                "/param_config_" + formatter.format(LocalDateTime.now()) + ".json");

        try (Writer writer = new FileWriter(file)) {
            var json = new GsonBuilder()
                    .setPrettyPrinting().create().toJson(bestParamConfig);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
