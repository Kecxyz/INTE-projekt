package grupp8;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import grupp8.Interpreter.BinaryOpNode;
import grupp8.Interpreter.CallNode;
import grupp8.Interpreter.CompareNode;
import grupp8.Interpreter.IntegerLiteralNode;
import grupp8.Interpreter.StringLiteralNode;
import grupp8.Interpreter.VarNode;
import grupp8.Lexer.TokenType;
import grupp8.Parser.ParserASTConverter;
import grupp8.Parser.ParserExpression;

class ParserASTConverterTest {


    @Test
    void convertsIntegerLiteral() {
        ParserExpression expression = new ParserExpression.IntegerLiteral(7);

        IntegerLiteralNode result = assertInstanceOf(
                IntegerLiteralNode.class, ParserASTConverter.toAST(expression)
        );

        assertEquals(7, result.getLiteralValue());
    }

    @Test
    void convertsStringLiteral() {
        ParserExpression expression = new ParserExpression.StringLiteral("Hello");

        StringLiteralNode result = assertInstanceOf(
                StringLiteralNode.class, ParserASTConverter.toAST(expression)
        );

        assertEquals("Hello", result.getLiteralValue());
    }

    @Test
    void convertsEmptyStringLiteral() {
        ParserExpression expression = new ParserExpression.StringLiteral("");

        StringLiteralNode result = assertInstanceOf(
                StringLiteralNode.class, ParserASTConverter.toAST(expression)
        );

        assertEquals("", result.getLiteralValue());
    }

    @Test
    void convertsVariableReference() {
        ParserExpression expression = new ParserExpression.VariableExpression("price");

        VarNode result = assertInstanceOf(VarNode.class, ParserASTConverter.toAST(expression));

        assertEquals("price", result.getVariableName());
    }


    @Test
    void convertsAddition() {
        ParserExpression expression = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(3),
                TokenType.PLUS,
                new ParserExpression.IntegerLiteral(2)
        );

        BinaryOpNode result = assertInstanceOf(BinaryOpNode.class, ParserASTConverter.toAST(expression));

        assertEquals('+', result.getOperatorSymbol());
        assertEquals(3, assertInstanceOf(IntegerLiteralNode.class, result.getLeftOperand()).getLiteralValue());
        assertEquals(2, assertInstanceOf(IntegerLiteralNode.class, result.getRightOperand()).getLiteralValue());
    }

    @Test
    void convertsSubtraction() {
        ParserExpression expression = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(10),
                TokenType.MINUS,
                new ParserExpression.IntegerLiteral(3)
        );

        BinaryOpNode result = assertInstanceOf(BinaryOpNode.class, ParserASTConverter.toAST(expression));

        assertEquals('-', result.getOperatorSymbol());
        assertEquals(10, assertInstanceOf(IntegerLiteralNode.class, result.getLeftOperand()).getLiteralValue());
        assertEquals(3, assertInstanceOf(IntegerLiteralNode.class, result.getRightOperand()).getLiteralValue());
    }

    @Test
    void convertsUnaryMinusToSubtractionFromZero() {
        ParserExpression expression = new ParserExpression.UnaryExpression(
                TokenType.MINUS, new ParserExpression.IntegerLiteral(7)
        );

        BinaryOpNode result = assertInstanceOf(BinaryOpNode.class, ParserASTConverter.toAST(expression));

        assertEquals('-', result.getOperatorSymbol());
        assertEquals(0, assertInstanceOf(IntegerLiteralNode.class, result.getLeftOperand()).getLiteralValue());
        assertEquals(7, assertInstanceOf(IntegerLiteralNode.class, result.getRightOperand()).getLiteralValue());
    }


    @Test
    void convertsFunctionCallAndPreservesArgumentOrder() {
        ParserExpression expression = new ParserExpression.CallExpression("subtract", List.of(
                new ParserExpression.IntegerLiteral(10),
                new ParserExpression.IntegerLiteral(3)
        ));

        CallNode result = assertInstanceOf(CallNode.class, ParserASTConverter.toAST(expression));

        assertEquals("subtract", result.getName());
        assertEquals(2, result.getArguments().size());
        IntegerLiteralNode first = assertInstanceOf(IntegerLiteralNode.class, result.getArguments().get(0));
        IntegerLiteralNode second = assertInstanceOf(IntegerLiteralNode.class, result.getArguments().get(1));
        assertEquals(10, first.getLiteralValue());
        assertEquals(3, second.getLiteralValue());
    }

    @Test
    void preservesNestedSubtraction() {
        ParserExpression inner = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(3),
                TokenType.MINUS,
                new ParserExpression.IntegerLiteral(2)
        );
        ParserExpression expression = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(10), TokenType.MINUS, inner
        );

        BinaryOpNode result = assertInstanceOf(BinaryOpNode.class, ParserASTConverter.toAST(expression));
        BinaryOpNode right = assertInstanceOf(BinaryOpNode.class, result.getRightOperand());

        assertEquals('-', result.getOperatorSymbol());
        assertEquals(10, assertInstanceOf(IntegerLiteralNode.class, result.getLeftOperand()).getLiteralValue());
        assertEquals('-', right.getOperatorSymbol());
        assertEquals(3, assertInstanceOf(IntegerLiteralNode.class, right.getLeftOperand()).getLiteralValue());
        assertEquals(2, assertInstanceOf(IntegerLiteralNode.class, right.getRightOperand()).getLiteralValue());
    }

    @Test
    void convertsUnaryMinusAroundAddition() {
        ParserExpression addition = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(3),
                TokenType.PLUS,
                new ParserExpression.IntegerLiteral(2)
        );
        ParserExpression expression = new ParserExpression.UnaryExpression(TokenType.MINUS, addition);

        BinaryOpNode result = assertInstanceOf(BinaryOpNode.class, ParserASTConverter.toAST(expression));
        BinaryOpNode operand = assertInstanceOf(BinaryOpNode.class, result.getRightOperand());

        assertEquals('-', result.getOperatorSymbol());
        assertEquals(0, assertInstanceOf(IntegerLiteralNode.class, result.getLeftOperand()).getLiteralValue());
        assertEquals('+', operand.getOperatorSymbol());
        assertEquals(3, assertInstanceOf(IntegerLiteralNode.class, operand.getLeftOperand()).getLiteralValue());
        assertEquals(2, assertInstanceOf(IntegerLiteralNode.class, operand.getRightOperand()).getLiteralValue());
    }


    @ParameterizedTest
    @CsvSource({
            "EQUAL_EQUAL, ==",
            "NOT_EQUAL, !=",
            "LESS_THAN, <",
            "GREATER_THAN, >",
            "LESS_EQUAL, <=",
            "GREATER_EQUAL, >="
    })
    void convertsComparisonOperators(TokenType operator, String expectedSymbol) {
        ParserExpression expression = new ParserExpression.BinaryExpression(
                new ParserExpression.VariableExpression("price"),
                operator,
                new ParserExpression.IntegerLiteral(100)
        );

        CompareNode result = assertInstanceOf(CompareNode.class, ParserASTConverter.toAST(expression));

        assertEquals(expectedSymbol, result.getComparisonOperator());
        assertEquals("price", assertInstanceOf(VarNode.class, result.getLeftOperand()).getVariableName());
        assertEquals(100, assertInstanceOf(IntegerLiteralNode.class, result.getRightOperand()).getLiteralValue());
    }


    @Test
    void rejectsUnsupportedUnaryOperator() {
        ParserExpression expression = new ParserExpression.UnaryExpression(
                TokenType.PLUS, new ParserExpression.IntegerLiteral(1)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> ParserASTConverter.toAST(expression)
        );

        assertTrue(error.getMessage().contains("'+'"));
    }

    @Test
    void rejectsUnsupportedBinaryOperator() {
        ParserExpression expression = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(1),
                TokenType.ASSIGN,
                new ParserExpression.IntegerLiteral(2)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> ParserASTConverter.toAST(expression)
        );

        assertTrue(error.getMessage().contains("'='"));
    }

    @Test
    void rejectsUnknownExpressionType() {
        ParserExpression expression = new ParserExpression() {};

        assertThrows(IllegalArgumentException.class, () -> ParserASTConverter.toAST(expression));
    }
}
