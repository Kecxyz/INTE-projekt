package grupp8;

import grupp8.Lexer.Token;
import grupp8.Lexer.TokenType;
import grupp8.Parser.Parser;
import grupp8.Parser.ParserException;
import grupp8.Parser.ParserExpression;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {


    @Test
    void parsesZero() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "0", 1, 1),
                new Token(TokenType.EOF, "", 1, 2)
        );
        Parser parser = new Parser(tokens);

        assertDoesNotThrow(() -> parser.parseCompleteExpression());
    }

    @Test
    void parsesIntegerLiteral() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "100", 1, 1),
                new Token(TokenType.EOF, "", 1, 4)
        );
        Parser parser = new Parser(tokens);

        ParserExpression result = parser.parseCompleteExpression();

        assertNotNull(result);
    }

    @Test
    void parsesStringLiteral() {
        List<Token> tokens = List.of(
                new Token(TokenType.STRING, "Hello", 1, 2),
                new Token(TokenType.EOF, "", 1, 8)
        );
        Parser parser = new Parser(tokens);

        ParserExpression result = parser.parseCompleteExpression();

        assertTrue(result instanceof ParserExpression.StringLiteral);
    }

    @Test
    void parsesEmptyStringLiteral() {
        List<Token> tokens = List.of(
                new Token(TokenType.STRING, "", 1, 2),
                new Token(TokenType.EOF, "", 1, 3)
        );
        Parser parser = new Parser(tokens);

        assertEquals(new ParserExpression.StringLiteral(""), parser.parseCompleteExpression());
    }

    @Test
    void parsesVariableReference() {
        List<Token> tokens = List.of(
                new Token(TokenType.IDENTIFIER, "price", 1, 1),
                new Token(TokenType.EOF, "", 1, 6)
        );
        Parser parser = new Parser(tokens);

        assertEquals(new ParserExpression.VariableExpression("price"), parser.parseCompleteExpression());
    }


    @Test
    void parsesAddition() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "3", 1, 1),
                new Token(TokenType.PLUS, "+", 1, 3),
                new Token(TokenType.INTEGER, "2", 1, 5),
                new Token(TokenType.EOF, "", 1, 6)
        );
        Parser parser = new Parser(tokens);
        ParserExpression expected = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(3),
                TokenType.PLUS,
                new ParserExpression.IntegerLiteral(2)
        );

        assertEquals(expected, parser.parseCompleteExpression());
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
        ParserExpression expected = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(10),
                TokenType.MINUS,
                new ParserExpression.IntegerLiteral(3)
        );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void parsesUnaryMinus() {
        List<Token> tokens = List.of(
                new Token(TokenType.MINUS, "-", 1, 1),
                new Token(TokenType.INTEGER, "3", 1, 2),
                new Token(TokenType.EOF, "", 1, 3)
        );
        Parser parser = new Parser(tokens);
        ParserExpression expected = new ParserExpression.UnaryExpression(
                TokenType.MINUS, new ParserExpression.IntegerLiteral(3)
        );

        assertEquals(expected, parser.parseCompleteExpression());
    }


    @Test
    void groupsSubtractionFromLeft() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "10", 1, 1),
                new Token(TokenType.MINUS, "-", 1, 4),
                new Token(TokenType.INTEGER, "3", 1, 6),
                new Token(TokenType.MINUS, "-", 1, 8),
                new Token(TokenType.INTEGER, "2", 1, 10),
                new Token(TokenType.EOF, "", 1, 11)
        );
        Parser parser = new Parser(tokens);
        ParserExpression firstSubtraction = new ParserExpression.BinaryExpression(
                new ParserExpression.IntegerLiteral(10),
                TokenType.MINUS,
                new ParserExpression.IntegerLiteral(3)
        );
        ParserExpression expected = new ParserExpression.BinaryExpression(
                firstSubtraction, TokenType.MINUS, new ParserExpression.IntegerLiteral(2)
        );

        assertEquals(expected, parser.parseCompleteExpression());
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
                new ParserExpression.IntegerLiteral(10), TokenType.MINUS, inner
        );

        assertEquals(expected, parser.parseCompleteExpression());
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
        ParserExpression expected = new ParserExpression.BinaryExpression(
                new ParserExpression.VariableExpression("price"),
                TokenType.GREATER_EQUAL,
                new ParserExpression.IntegerLiteral(100)
        );

        assertEquals(expected, parser.parseCompleteExpression());
    }


    @Test
    void rejectsEmptyTokenList() {
        ParserException error = assertThrows(ParserException.class, () -> new Parser(List.of()));

        assertTrue(error.getMessage().contains("slut på inmatningen"));
    }

    @Test
    void rejectsTokenListWithoutEndOfFile() {
        List<Token> tokens = List.of(new Token(TokenType.INTEGER, "1", 1, 1));

        ParserException error = assertThrows(ParserException.class, () -> new Parser(tokens));

        assertTrue(error.getMessage().contains("slut på inmatningen"));
    }

    @Test
    void rejectsEmptyExpression() {
        Parser parser = new Parser(List.of(new Token(TokenType.EOF, "", 1, 1)));

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("slut på inmatningen"));
        assertTrue(error.getMessage().contains("1:1"));
    }

    @Test
    void rejectsNonNumericIntegerToken() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "abc", 2, 4),
                new Token(TokenType.EOF, "", 2, 7)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("abc"));
        assertTrue(error.getMessage().contains("2:4"));
    }

    @Test
    void rejectsEmptyIntegerToken() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "", 1, 1),
                new Token(TokenType.EOF, "", 1, 1)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("1:1"));
    }

    @Test
    void rejectsPlusWithoutOperand() {
        List<Token> tokens = List.of(
                new Token(TokenType.PLUS, "+", 2, 5),
                new Token(TokenType.EOF, "", 2, 6)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("fick '+'"));
        assertTrue(error.getMessage().contains("2:5"));
    }

    @Test
    void rejectsMinusWithoutOperand() {
        List<Token> tokens = List.of(
                new Token(TokenType.MINUS, "-", 1, 1),
                new Token(TokenType.EOF, "", 1, 2)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("slut på inmatningen"));
        assertTrue(error.getMessage().contains("1:2"));
    }

    @Test
    void rejectsMissingRightOperand() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "3", 1, 1),
                new Token(TokenType.PLUS, "+", 1, 3),
                new Token(TokenType.EOF, "", 1, 4)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("slut på inmatningen"));
        assertTrue(error.getMessage().contains("1:4"));
    }


    @Test
    void parsesLargestIntegerLiteral() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "2147483647", 1, 1),
                new Token(TokenType.EOF, "", 1, 11)
        );
        Parser parser = new Parser(tokens);

        assertEquals(new ParserExpression.IntegerLiteral(Integer.MAX_VALUE), parser.parseCompleteExpression());
    }

    @Test
    void rejectsIntegerAboveMaximum() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "2147483648", 1, 1),
                new Token(TokenType.EOF, "", 1, 11)
        );
        Parser parser = new Parser(tokens);

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("2147483648"));
        assertTrue(error.getMessage().contains("1:1"));
    }
}
