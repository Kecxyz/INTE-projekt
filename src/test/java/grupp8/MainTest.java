package grupp8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {
    @TempDir
    Path directory;

    @Test
    void runsDefaultProgram() throws IOException {
        String output = runMain();

        assertEquals("5050" + System.lineSeparator(), output);
    }

    @Test
    void runsWholeProgramFromFile() throws IOException {
        Path file = directory.resolve("java-inte-testfall.txt");
        Files.writeString(file, """
                function add(a, b) { return a + b; }
                let total = 0;
                while (total < 3) {
                    total = add(total, 1);
                }
                if (total == 3) {
                    print("Färdig");
                    print(total);
                }
                """);

        String output = runMain(file.toString());

        assertEquals("Färdig" + System.lineSeparator() + "3" + System.lineSeparator(), output);
    }

    private String runMain(String... arguments) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previousOutput = System.out;
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            Main.main(arguments);
        } finally {
            System.setOut(previousOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
