package se.ariful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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
}