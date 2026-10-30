package grupp8;

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
    public void testDefineAndGetVariable() {
        env.defineVariable("x", 42);
        assertEquals(42, env.getVariable("x"));
    }

    @Test
    public void testSetAndGetVariable() {
        env.defineVariable("x", 10);
        env.setVariable("x", 42);
        assertEquals(42, env.getVariable("x"));
    }

    @Test
    public void testUpdateExistingVariable() {
        env.defineVariable("count", 10);
        env.setVariable("count", 20); // Overwrites/updates
        assertEquals(20, env.getVariable("count"));
    }

    @Test
    public void testUndefinedVariableLookupThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            env.getVariable("nonExistent");
        });
    }

    @Test
    public void testEnvironmentParentLookup() {
        Environment global = new Environment();
        global.defineVariable("x", 10);

        Environment local = new Environment(global);
        // Should find 'x' from the parent/global environment
        assertEquals(10, local.getVariable("x"));
    }

    @Test
    public void testEnvironmentVariableUpdateInParent() {
        Environment global = new Environment();
        global.defineVariable("x", 10);

        Environment local = new Environment(global);
        // Setting a variable present in parent scope should update the parent
        local.setVariable("x", 20);
        assertEquals(20, global.getVariable("x"));
    }

    @Test
    public void testPrintOutputDoesNotThrow() {
        // Simple verification for printOutput method coverage
        assertDoesNotThrow(() -> env.printOutput("Test output"));
    }
}