package se.ariful;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests interpreter nodes using visitor pattern.
public class InterpreterTest {

    private Environment runtimeEnvironment;
    private Interpreter interpreter;

    @BeforeEach
    public void setUp() {
        runtimeEnvironment = new Environment();
        interpreter = new Interpreter(runtimeEnvironment);
    }

    @Test
    public void testIntegerAdditionRule() {
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(5),
                '+',
                new IntegerLiteralNode(3)
        );
        Object result = node.accept(interpreter);
        assertTrue(result instanceof Integer);
        assertEquals(8, result);
    }

    @Test
    public void testMixedAdditionLeftStringRule() {
        ASTNode node = new BinaryOpNode(
                new StringLiteralNode("Score: "),
                '+',
                new IntegerLiteralNode(10)
        );
        Object result = node.accept(interpreter);
        assertTrue(result instanceof String);
        assertEquals("Score: 10", result);
    }

    @Test
    public void testMixedAdditionRightStringRule() {
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(5),
                '+',
                new StringLiteralNode(" apples")
        );
        Object result = node.accept(interpreter);
        assertTrue(result instanceof String);
        assertEquals("5 apples", result);
    }

    @Test
    public void testPureStringConcatenationRule() {
        ASTNode node = new BinaryOpNode(
                new StringLiteralNode("Hello "),
                '+',
                new StringLiteralNode("World")
        );
        Object result = node.accept(interpreter);
        assertTrue(result instanceof String);
        assertEquals("Hello World", result);
    }

    @Test
    public void testSubtraction() {
        ASTNode node = new BinaryOpNode(
                new IntegerLiteralNode(10),
                '-',
                new IntegerLiteralNode(4)
        );
        Object result = node.accept(interpreter);
        assertEquals(6, result);
    }

    @Test
    public void testVariableDeclarationAndLookup() {
        ASTNode letNode = new LetNode("x", new IntegerLiteralNode(42));
        letNode.accept(interpreter);

        ASTNode varNode = new VarNode("x");
        Object result = varNode.accept(interpreter);
        assertEquals(42, result);
    }

    @Test
    public void testComparisonGreaterThan() {
        ASTNode compare = new CompareNode(
                new IntegerLiteralNode(10),
                ">",
                new IntegerLiteralNode(5)
        );
        Object result = compare.accept(interpreter);
        assertTrue((Boolean) result);
    }

    @Test
    public void testAssignmentNode() {
        // Tests AssignNode execution
        ASTNode assign = new AssignNode("total_1", new IntegerLiteralNode(25));
        Object result = assign.accept(interpreter);
        assertEquals(25, result);
    }

    @Test
    public void testPrintNode() {
        // Tests PrintNode execution
        ASTNode print = new PrintNode(new StringLiteralNode("Hello Test"));
        Object result = print.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testBlockNode() {
        // Tests BlockNode sequential execution
        ASTNode block = new BlockNode(Arrays.asList(
                new LetNode("a", new IntegerLiteralNode(10)),
                new LetNode("b", new IntegerLiteralNode(20))
        ));
        Object result = block.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testIfNodeBranches() {
        // Tests IfNode 'then' branch and 'else' branch
        ASTNode ifTrue = new IfNode(
                new CompareNode(new IntegerLiteralNode(5), "<", new IntegerLiteralNode(10)),
                new PrintNode(new StringLiteralNode("True Path")),
                new PrintNode(new StringLiteralNode("False Path"))
        );
        Object res1 = ifTrue.accept(interpreter);
        assertNull(res1);

        ASTNode ifFalse = new IfNode(
                new CompareNode(new IntegerLiteralNode(15), "<", new IntegerLiteralNode(10)),
                new PrintNode(new StringLiteralNode("True Path")),
                new PrintNode(new StringLiteralNode("False Path"))
        );
        Object res2 = ifFalse.accept(interpreter);
        assertNull(res2);
    }

    @Test
    public void testWhileNodeLoop() {
        // Tests WhileNode execution and termination
        ASTNode loopProgram = new BlockNode(Arrays.asList(
                new LetNode("counter", new IntegerLiteralNode(2)),
                new WhileNode(
                        new CompareNode(new VarNode("counter"), ">", new IntegerLiteralNode(0)),
                        new BlockNode(Arrays.asList(
                                new AssignNode("counter", new BinaryOpNode(new VarNode("counter"), '-', new IntegerLiteralNode(1)))
                        ))
                )
        ));
        Object result = loopProgram.accept(interpreter);
        assertNull(result);
    }
}