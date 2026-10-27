package se.ariful;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests variable storage and lookups in Environment.
public class EnvironmentTest {

    private Environment env;

    @BeforeEach
    public void setUp() {
        env = new Environment();
    }

    @Test
    public void testSetAndGetVariable() {
        env.setVariable("x", 42);
        assertEquals(42, env.getVariable("x"));
    }

    @Test
    public void testUpdateExistingVariable() {
        env.setVariable("count", 10);
        env.setVariable("count", 20); // Overwrites/updates
        assertEquals(20, env.getVariable("count"));
    }

    @Test
    public void testUndefinedVariableLookupThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            env.getVariable("nonExistent");
        });
    }
}