package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String baseDir;
        if (args.length > 0) {
            baseDir = args[0]; // Use the provided command-line argument
        } else {
            baseDir = System.getProperty("user.dir") + "/src/test/"; // Use the default path
        }
        System.out.println("Base Directory: " + baseDir);

        try {
            Files.list(Paths.get(baseDir))
                    .filter(Files::isDirectory)
                    .sorted()
                    .forEach(Main::processStepDirectory);
        } catch (IOException e) {
            System.out.println("Error reading directory: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void processStepDirectory(Path stepDir) {
        System.out.println("Processing step directory: " + stepDir);

        try {
            Files.list(stepDir)
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                    .forEach(Main::processJsonFile);
        } catch (IOException e) {
            System.out.println("Error reading step directory: " + stepDir + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred in directory: " + stepDir + " - " + e.getMessage());
        }
    }

    private static void processJsonFile(Path filePath) {
        System.out.println("Processing file: " + filePath);

        try {
            String jsonContent = new String(Files.readAllBytes(filePath));
            System.out.println("Testing JSON from file: " + filePath);

            JSONLexer lexer = new JSONLexer(jsonContent);
            List<Token> tokens = lexer.tokenize();
            JSONParser parser = new JSONParser(tokens);
            parser.parse();

            System.out.println("Valid JSON\n");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid JSON in file: " + filePath + " - " + e.getMessage());
        }
    }
}