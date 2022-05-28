package de.dhbw.mosbach.se.si.app3;

import com.google.gson.GsonBuilder;
import de.dhbw.mosbach.se.si.app2.parmeter.ParameterConfiguration;
import de.dhbw.mosbach.se.si.app3.searcher.AntColonyParameterSearcher;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    static {
        // Setup logging.
        try (var stream = de.dhbw.mosbach.se.si.app2.App.class
                .getClassLoader()
                .getResourceAsStream(Configuration.INSTANCE.loggingPropertiesFile)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Get best parameter config.
        var bestParamConfig = new AntColonyParameterSearcher().run();
        writeToJSON(bestParamConfig);
    }

    private static void writeToJSON(ParameterConfiguration paramConfig) {
        var rootDirectory = new File("data");
        if (!rootDirectory.mkdirs()) {
            throw new RuntimeException("Directories cannot be created.");
        }

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm");
        var file = new File(rootDirectory.getPath() +
                "/best_" + formatter.format(LocalDateTime.now()) + ".json");

        try (Writer writer = new FileWriter(file)) {
            LOGGER.log(Level.INFO, "Write configuration to " + file.getAbsolutePath());
            var json = new GsonBuilder()
                    .setPrettyPrinting().create().toJson(paramConfig);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
