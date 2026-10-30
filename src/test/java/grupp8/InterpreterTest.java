package grupp8;

import java.util.*;

import grupp8.Interpreter.*;
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
        ASTNode assign = new AssignNode("total_1", new IntegerLiteralNode(25));
        Object result = assign.accept(interpreter);
        assertEquals(25, result);
    }

    @Test
    public void testPrintNode() {
        ASTNode print = new PrintNode(new StringLiteralNode("Hello Test"));
        Object result = print.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testBlockNode() {
        ASTNode block = new BlockNode(Arrays.asList(
                new LetNode("a", new IntegerLiteralNode(10)),
                new LetNode("b", new IntegerLiteralNode(20))
        ));
        Object result = block.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testIfNodeBranches() {
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

    @Test
    public void testFunctionCallAndReturn() {
        ASTNode funcDef = new FunctionNode(
                "add",
                Arrays.asList("a", "b"),
                new BlockNode(Arrays.asList(
                        new ReturnNode(new BinaryOpNode(new VarNode("a"), '+', new VarNode("b")))
                ))
        );
        funcDef.accept(interpreter);

        ASTNode callNode = new CallNode("add", Arrays.asList(
                new IntegerLiteralNode(10),
                new IntegerLiteralNode(32)
        ));
        Object result = callNode.accept(interpreter);
        assertEquals(42, result);
    }

    @Test
    public void testFunctionWithoutExplicitReturn() {
        ASTNode funcDef = new FunctionNode(
                "doNothing",
                Collections.emptyList(),
                new BlockNode(Arrays.asList(
                        new PrintNode(new StringLiteralNode("hello"))
                ))
        );
        funcDef.accept(interpreter);

        ASTNode callNode = new CallNode("doNothing", Collections.emptyList());
        Object result = callNode.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testReturnWithoutExpression() {
        ASTNode funcDef = new FunctionNode(
                "emptyReturn",
                Collections.emptyList(),
                new BlockNode(Arrays.asList(
                        new ReturnNode(null)
                ))
        );
        funcDef.accept(interpreter);

        ASTNode callNode = new CallNode("emptyReturn", Collections.emptyList());
        Object result = callNode.accept(interpreter);
        assertNull(result);
    }

    @Test
    public void testDuplicateFunctionDefinitionThrowsException() {
        ASTNode funcDef1 = new FunctionNode("f", Collections.emptyList(), new BlockNode(Collections.emptyList()));
        ASTNode funcDef2 = new FunctionNode("f", Collections.emptyList(), new BlockNode(Collections.emptyList()));

        funcDef1.accept(interpreter);
        assertThrows(RuntimeException.class, () -> funcDef2.accept(interpreter));
    }

    @Test
    public void testUndefinedFunctionCallThrowsException() {
        ASTNode callNode = new CallNode("nonExistent", Collections.emptyList());
        assertThrows(RuntimeException.class, () -> callNode.accept(interpreter));
    }

    @Test
    public void testFunctionArgumentMismatchThrowsException() {
        ASTNode funcDef = new FunctionNode(
                "takesOne",
                Collections.singletonList("x"),
                new BlockNode(Collections.emptyList())
        );
        funcDef.accept(interpreter);

        ASTNode callNode = new CallNode("takesOne", Arrays.asList(new IntegerLiteralNode(1), new IntegerLiteralNode(2)));
        assertThrows(RuntimeException.class, () -> callNode.accept(interpreter));
    }

    @Test
    public void testUnknownBinaryOperatorThrowsException() {
        ASTNode node = new BinaryOpNode(new IntegerLiteralNode(5), '*', new IntegerLiteralNode(3));
        assertThrows(RuntimeException.class, () -> node.accept(interpreter));
    }

    @Test
    public void testIntegerConversionExceptionInBinaryOp() {
        ASTNode node = new BinaryOpNode(new StringLiteralNode("abc"), '-', new IntegerLiteralNode(3));
        assertThrows(RuntimeException.class, () -> node.accept(interpreter));
    }

    @Test
    public void testUnknownIntegerComparisonOperatorThrowsException() {
        ASTNode compare = new CompareNode(new IntegerLiteralNode(5), "???", new IntegerLiteralNode(3));
        assertThrows(RuntimeException.class, () -> compare.accept(interpreter));
    }

    @Test
    public void testInvalidStringComparisonOperatorThrowsException() {
        ASTNode compare = new CompareNode(new StringLiteralNode("a"), "<=", new StringLiteralNode("b"));
        assertThrows(RuntimeException.class, () -> compare.accept(interpreter));
    }

    @Test
    public void testConditionNotBooleanThrowsException() {
        ASTNode ifNode = new IfNode(
                new IntegerLiteralNode(42),
                new PrintNode(new StringLiteralNode("yes")),
                null
        );
        assertThrows(RuntimeException.class, () -> ifNode.accept(interpreter));
    }
}