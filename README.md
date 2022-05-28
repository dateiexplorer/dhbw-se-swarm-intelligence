# dhbw-se-swarm-intelligence

An implementation of swarm intelligence algorithms to solve the TSP in various
ways as a part of the Advanced Software Engineering lecture.

## Disclaimer

This repository is meant for educational purposes and should not be used in any
productive scenarios.

## Compile

To compile an app you need Gradle (version 7.3+) and a working JDK Installation
(version 17+).

Build all with:
```
gradle build
```

Build a specific app, e.g. 'app1' with:
```
gradle app1:build
```

Run all apps with:
```
gradle run
```

Run a specific app, e.g. 'app1' with:
```
gradle app1:run
```

## Generate executable JAR files

To generate executable JAR file for all apps run:
```
gradle jar
```

Afterwards you can run an application for example with:
```
java -jar app1/build/libs/app1.jar
```