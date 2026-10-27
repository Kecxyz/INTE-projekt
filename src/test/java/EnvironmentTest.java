

import org.junit.jupiter.api.Test;
import se.ariful.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentTest {

    @Test
    void shouldStoreVariable() {

        Environment env = new Environment();

        env.setVariable("x", 10);

        assertEquals(10, env.getVariable("x"));
    }
    @Test
    void shouldThrowExceptionForUnknownVariable() {

        Environment env = new Environment();

        assertThrows(
                RuntimeException.class,
                () -> env.getVariable("unknown")
        );
    }

    @Test
    void shouldPrint() {
        Environment env = new Environment();
        env.printOutput("Hello");
    }
}