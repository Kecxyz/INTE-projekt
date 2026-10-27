package se.ariful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ParserIntegrationTest {
    @Test 
    void parsesSubtractionOfNegativeNumber() {
        Lexer lexer = new Lexer("10 - -3");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression negativeThree = new ParserExpression.UnaryExpression(TokenType.MINUS, new ParserExpression.IntegerLiteral(3));

        ParserExpression expected = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(10),
            TokenType.MINUS,
            negativeThree
        );

        assertEquals(expected, parser.parseExpression());
    }

    @Test 
    void parsesNegatedParenthesizedExpression() {
        Lexer lexer = new Lexer("-(3 + 2)");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression addition = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(3),
            TokenType.PLUS,
            new ParserExpression.IntegerLiteral(2)
        );

        ParserExpression expected = new ParserExpression.UnaryExpression(
            TokenType.MINUS,
            addition
        );

        assertEquals(expected, parser.parseExpression());
    }

    @Test
    void parsesComparisonOperators() {
        assertComparison("3 < 5", TokenType.LESS_THAN);
        assertComparison("3 > 5", TokenType.GREATER_THAN);
        assertComparison("3 <= 5", TokenType.LESS_EQUAL);
        assertComparison("3 >= 5", TokenType.GREATER_EQUAL);
    }

    private void assertComparison(String source, TokenType operator) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        operator,
                        new ParserExpression.IntegerLiteral(5)
                );

        assertEquals(expected, parser.parseExpression(), source);
    }

    @Test
    void groupsArithmeticBeforeComparison() {
        Lexer lexer = new Lexer("price + 20 >= total - 10");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression left =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.VariableExpression("price"),
                        TokenType.PLUS,
                        new ParserExpression.IntegerLiteral(20)
                );

        ParserExpression right =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.VariableExpression("total"),
                        TokenType.MINUS,
                        new ParserExpression.IntegerLiteral(10)
                );

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        left,
                        TokenType.GREATER_EQUAL,
                        right
                );

        assertEquals(expected, parser.parseExpression());
    }

    @Test
    void parsesEqualityOperators() {
        assertComparison("3 == 5", TokenType.EQUAL_EQUAL);
        assertComparison("3 != 5", TokenType.NOT_EQUAL);
    }

    @Test
    void groupsComparisonBeforeEquality() {
        Lexer lexer = new Lexer("3 < 5 == 2 < 4");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression left =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        TokenType.LESS_THAN,
                        new ParserExpression.IntegerLiteral(5)
                );

        ParserExpression right =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(2),
                        TokenType.LESS_THAN,
                        new ParserExpression.IntegerLiteral(4)
                );

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        left,
                        TokenType.EQUAL_EQUAL,
                        right
                );

        assertEquals(expected, parser.parseExpression());
    }



    @Test
    void rejectsMissingEqualityOperand() {
        assertUnexpectedEnd("3 ==");
    }

    @Test
    void rejectsMissingClosingParenthesis() {
        assertUnexpectedEnd("(3 + 2");
    }


    @Test
    void rejectsEmptyExpression() {
        assertUnexpectedEnd("");
    }

    private void assertUnexpectedEnd(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseExpression()
        );

        assertTrue(error.getMessage().contains("EOF"));
    }

    @Test
    void rejectsExtraTokenAfterExpression() {
        Lexer lexer = new Lexer("3 4");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseCompleteExpression()
        );

        assertTrue(error.getMessage().contains("INTEGER"));
        assertTrue(error.getMessage().contains("1:3"));
    }

    @Test
    void parsesCompleteExpression() {
        Lexer lexer = new Lexer("3 + 2");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        TokenType.PLUS,
                        new ParserExpression.IntegerLiteral(2)
                );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void acceptsLetStatement() {
        Lexer lexer = new Lexer("let price = 100 + 20;");
        Parser parser = new Parser(lexer.tokenize());

        assertDoesNotThrow(() -> parser.parseStatement());
    }

    @Test
    void rejectsLetWithoutSemicolon() {
        Lexer lexer = new Lexer("let price = 100 + 20");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseStatement()
        );

        assertTrue(error.getMessage().contains("';'"));
    }
}