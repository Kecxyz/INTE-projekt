package se.ariful;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    private Environment env;

    @BeforeEach
    public void setUp() {
        env = new Environment();
    }

    @Test
    public void testIntegerAdditionRule() {
        // Test Rule 1: Int + Int -> Integer Addition
        // 5 + 3 = 8
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(5),
                '+',
                new IntegerLiteralNode(3)
        );
        Object result = node.evaluate(env);
        assertTrue(result instanceof Integer);
        assertEquals(8, result);
    }

    @Test
    public void testMixedAdditionLeftStringRule() {
        // Test Rule 2: String + Int -> String Concatenation
        // "Score: " + 10 = "Score: 10"
        ASTNode node = new BinaryOpNode(
                new StringLiteralNode("Score: "),
                '+',
                new IntegerLiteralNode(10)
        );
        Object result = node.evaluate(env);
        assertTrue(result instanceof String);
        assertEquals("Score: 10", result);
    }

    @Test
    public void testMixedAdditionRightStringRule() {
        // Test Rule 3: Int + String -> String Concatenation
        // 5 + " apples" = "5 apples"
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(5),
                '+',
                new StringLiteralNode(" apples")
        );
        Object result = node.evaluate(env);
        assertTrue(result instanceof String);
        assertEquals("5 apples", result);
    }

    @Test
    public void testPureStringConcatenationRule() {
        // Test Rule 4: String + String -> String Concatenation
        // "Hello " + "World" = "Hello World"
        ASTNode node = new BinaryOpNode(
                new StringLiteralNode("Hello "),
                '+',
                new StringLiteralNode("World")
        );
        Object result = node.evaluate(env);
        assertTrue(result instanceof String);
        assertEquals("Hello World", result);
    }

    @Test
    public void testSubtraction() {
        // 10 - 4 = 6
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(10),
                '-',
                new IntegerLiteralNode(4)
        );
        Object result = node.evaluate(env);
        assertEquals(6, result);
    }

    @Test
    public void testVariableDeclarationAndLookup() {
        // let x = 42; followed by referencing x
        ASTNode letNode = new LetNode("x", new IntegerLiteralNode(42));
        letNode.evaluate(env);

        ASTNode varNode = new VarNode("x");
        Object result = varNode.evaluate(env);
        assertEquals(42, result);
    }

    @Test
    public void testComparisonGreaterThan() {
        // 10 > 5 -> true
        ASTNode compare = new CompareNode(
                new IntegerLiteralNode(10),
                ">",
                new IntegerLiteralNode(5)
        );
        Object result = compare.evaluate(env);
        assertTrue((Boolean) result);
    }
}