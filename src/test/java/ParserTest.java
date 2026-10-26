package se.ariful;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void parsesIntegerLiteral() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "100", 1, 1),
            new Token(TokenType.EOF, "", 1, 4) 
        );

        Parser parser = new Parser(tokens);

        ParserExpression result = parser.parseExpression();

        assertEquals(new ParserExpression.IntegerLiteral(100), result);
    }

    @Test
    void rejectsPlusWithoutOperand() {
        List<Token> tokens = List.of(
            new Token(TokenType.PLUS, "+", 2, 5),
            new Token(TokenType.EOF, "", 2, 6)
        );
        Parser  parser = new Parser(tokens);
        ParserException error = assertThrows(
            ParserException.class, 
            () -> parser.parseExpression()
        );

        assertTrue(error.getMessage().contains("2:5"));
        assertTrue(error.getMessage().contains("PLUS"));
    }

    @Test
    void parsesAddition() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "3", 1, 1),
            new Token(TokenType.PLUS, "+",  1,3),
            new Token(TokenType.INTEGER, "2", 1,5),
            new Token(TokenType.EOF, "", 1,6)
        );
        Parser parser = new Parser(tokens);
        ParserExpression result = parser.parseExpression();

        ParserExpression expected = 
            new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(3), TokenType.PLUS, new ParserExpression.IntegerLiteral(2) 
        );
        
        assertEquals(expected, result);
    }

    @Test
    void parsesSubtraction() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "10", 1, 1),
            new Token(TokenType.MINUS, "-", 1, 4),
            new Token(TokenType.INTEGER, "3", 1, 6),
            new Token(TokenType.EOF, "", 1, 7)
        );

        Parser parser = new Parser(tokens);
        ParserExpression result = parser.parseExpression();
        ParserExpression exprected = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(10),
            TokenType.MINUS,
            new ParserExpression.IntegerLiteral(3)
        );

        assertEquals(exprected, result);
        
    }

    @Test 
    void groupsSubtractionFromLeft() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "10", 1,1),
            new Token(TokenType.MINUS, "-", 1,4),
            new Token(TokenType.INTEGER, "3", 1,6),
            new Token(TokenType.MINUS, "-", 1,8),
            new Token(TokenType.INTEGER, "2", 1,10),
            new Token(TokenType.EOF, "", 1,11)
        );

        Parser parser = new Parser(tokens);
        ParserExpression firstSubraction = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(10),
                TokenType.MINUS,
                new ParserExpression.IntegerLiteral(3)
            );
        
            ParserExpression expected = new ParserExpression.BinaryExpression (
                firstSubraction, TokenType.MINUS, new ParserExpression.IntegerLiteral(2)
            );

        assertEquals(expected, parser.parseExpression());
    }

    @Test
    void rejectsMissingRightOperand() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "3", 1, 1),
            new Token(TokenType.PLUS, "+", 1, 3),
            new Token(TokenType.EOF, "", 1, 4)
        );

        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(
            ParserException.class, () -> parser.parseExpression()
        );

        assertTrue(error.getMessage().contains("EOF"));
        assertTrue(error.getMessage().contains("1:4"));
        
    }

    @Test 
    void parsesStringLiteral() {
        List<Token> tokens = List.of(
            new Token(TokenType.STRING, "Hello", 1, 2),
            new Token(TokenType.EOF, "", 1, 8)
        );
        Parser parser = new Parser(tokens);
        ParserExpression result = parser.parseExpression();

        assertEquals(new ParserExpression.StringLiteral("Hello"), result);
    }

    @Test
    void parsesVaribaleReference() {
        List<Token> tokens = List.of(
            new Token(TokenType.IDENTIFIER, "price", 1, 1),
            new Token(TokenType.EOF, "", 1, 6)
        );

        Parser parser = new Parser(tokens);
        ParserExpression result = parser.parseExpression();
        assertEquals(new ParserExpression.VariableExpression("price"), result);
    }

    @Test 
    void respectsParentheses() {
        List<Token> tokens = List.of(
            new Token(TokenType.INTEGER, "10", 1, 1),
            new Token(TokenType.MINUS, "-", 1, 4),
            new Token(TokenType.LEFT_PAREN, "(", 1, 6),
            new Token(TokenType.INTEGER, "3", 1, 7),
            new Token(TokenType.MINUS, "-", 1, 9),
            new Token(TokenType.INTEGER, "2", 1, 11),
            new Token(TokenType.RIGHT_PAREN, ")", 1, 12),
            new Token(TokenType.EOF, "", 1, 13)
        );

        Parser parser = new Parser(tokens);
        ParserExpression inner = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(3),
            TokenType.MINUS,
            new ParserExpression.IntegerLiteral(2)
        );

        ParserExpression expected = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(10),
            TokenType.MINUS,
            inner
        );

        assertEquals(expected, parser.parseExpression());
    }

    @Test
    void parsesUnaryMinus() {
        List<Token> tokens = List.of(
            new Token(TokenType.MINUS, "-", 1, 1),
            new Token(TokenType.INTEGER, "3", 1, 2),
            new Token(TokenType.EOF, "", 1, 3)
        );

        Parser parser = new Parser(tokens);

        ParserExpression expected = new ParserExpression.UnaryExpression(TokenType.MINUS, new ParserExpression.IntegerLiteral(3));
        assertEquals(expected, parser.parseExpression());
    }

    @Test 
    void parsesGreaterEqualComparison() {
        List<Token> tokens = List.of(
                new Token(TokenType.IDENTIFIER, "price", 1, 1),
                new Token(TokenType.GREATER_EQUAL, ">=", 1, 7),
                new Token(TokenType.INTEGER, "100", 1, 10),
                new Token(TokenType.EOF, "", 1, 13)
        );

        Parser parser = new Parser(tokens);

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.VariableExpression("price"),
                        TokenType.GREATER_EQUAL,
                        new ParserExpression.IntegerLiteral(100) 
                );

        assertEquals(expected, parser.parseExpression());
    }
}
