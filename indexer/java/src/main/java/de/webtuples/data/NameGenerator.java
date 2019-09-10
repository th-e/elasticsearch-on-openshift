package de.webtuples.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NameGenerator {

    private static final String FIRSTNAMES_FILENAME = "firstnames.dat";
    private static final String LASTNAMES_FILENAME = "lastnames.dat";


    private List<String> firstnames = new ArrayList<>();
    private List<String> lastnames = new ArrayList<>();
    private Random randomGenerator = new Random();


    public NameGenerator() {
        try {
            firstnames = readFileInfoList(FIRSTNAMES_FILENAME);
            lastnames = readFileInfoList(LASTNAMES_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readFileInfoList(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
        try (Stream<String> stream = Files.lines( file.toPath())) {
            return stream
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }

    public String getRandomFirstName(){
        return firstnames.get(randomGenerator.nextInt(firstnames.size()));
    }

    public String getRandomLastName(){
        return lastnames.get(randomGenerator.nextInt(lastnames.size()));
    }
}
