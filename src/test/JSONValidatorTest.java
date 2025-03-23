package test;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import main.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JSONValidatorTest {
    private static final String TEST_RESOURCES_DIR = "src/test/resources/";

    @Test
    void testJsonFiles() throws IOException {
        Path testDir = Paths.get(TEST_RESOURCES_DIR);
        assertTrue(Files.exists(testDir), "Test directory does not exist: " + testDir);

        Files.list(testDir)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                .forEach(this::validateJsonFile);
    }

    private void validateJsonFile(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        boolean shouldPass = fileName.startsWith("pass") || fileName.startsWith("valid");
        boolean shouldFail = fileName.startsWith("fail") || fileName.startsWith("invalid");

        assertFalse(shouldPass && shouldFail, "File name contains conflicting keywords: " + fileName);

        try {
            String jsonContent = Files.readString(filePath);
            JSONLexer lexer = new JSONLexer(jsonContent);
            List<Token> tokens = lexer.tokenize();
            JSONParser parser = new JSONParser(tokens);
            parser.parse();

            assertTrue(shouldPass, "Expected valid JSON but found invalid: " + fileName);
        } catch (Exception e) {
            assertTrue(shouldFail, "Expected invalid JSON but found valid: " + fileName);
        }
    }
}
