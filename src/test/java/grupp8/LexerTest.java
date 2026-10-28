package grupp8;

import grupp8.Lexer.Lexer;
import grupp8.Lexer.LexerException;
import grupp8.Lexer.Token;
import grupp8.Lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class LexerTest {
    @Test
    void shouldTokenizeInteger() {
        Lexer lexer = new Lexer("123");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals("123", tokens.get(0).getValue());
        assertEquals(TokenType.EOF, tokens.get(1).getType());
    }

    @Test
    void shouldTokenizeAddition() {
        Lexer lexer = new Lexer("12 + 34");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals("12", tokens.get(0).getValue());

        assertEquals(TokenType.PLUS, tokens.get(1).getType());

        assertEquals(TokenType.INTEGER, tokens.get(2).getType());
        assertEquals("34", tokens.get(2).getValue());
    }

    @Test
    void shouldTokenizeSubtraction() {
        Lexer lexer = new Lexer("10 - 3");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals(TokenType.MINUS, tokens.get(1).getType());
        assertEquals(TokenType.INTEGER, tokens.get(2).getType());
    }

    @Test
    void shouldSkipWhitespace() {
        Lexer lexer = new Lexer("   123   +   4   ");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals(TokenType.PLUS, tokens.get(1).getType());
        assertEquals(TokenType.INTEGER, tokens.get(2).getType());
        assertEquals(TokenType.EOF, tokens.get(3).getType());
    }


    @Test
    void shouldRecognizeKeywords() {
        Lexer lexer = new Lexer("let if else while print");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LET, tokens.get(0).getType());
        assertEquals(TokenType.IF, tokens.get(1).getType());
        assertEquals(TokenType.ELSE, tokens.get(2).getType());
        assertEquals(TokenType.WHILE, tokens.get(3).getType());
        assertEquals(TokenType.PRINT, tokens.get(4).getType());
    }

    @Test
    void shouldRecognizeIdentifier() {
        Lexer lexer = new Lexer("myVariable");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("myVariable", tokens.get(0).getValue());
    }

    @Test
    void keywordInsideIdentifierShouldRemainIdentifier() {
        Lexer lexer = new Lexer("letter");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("letter", tokens.get(0).getValue());
    }


    @Test
    void shouldTokenizeString() {
        Lexer lexer = new Lexer("\"Hello world\"");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("Hello world", tokens.get(0).getValue());
    }

    @Test
    void shouldTokenizeAssignmentAndSemicolon() {
        Lexer lexer = new Lexer("let x = 5;");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LET, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals(TokenType.ASSIGN, tokens.get(2).getType());
        assertEquals(TokenType.INTEGER, tokens.get(3).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(4).getType());
    }


    @Test
    void shouldTokenizeComparisonOperators() {
        Lexer lexer = new Lexer("== != < > <= >=");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(0).getType());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(1).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(2).getType());
        assertEquals(TokenType.GREATER_THAN, tokens.get(3).getType());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(4).getType());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(5).getType());
    }

    @Test
    void shouldTokenizeBracesAndParentheses() {
        Lexer lexer = new Lexer("(x) { }");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LEFT_PAREN, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(2).getType());
        assertEquals(TokenType.LEFT_BRACE, tokens.get(3).getType());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(4).getType());
    }


    @Test
    void shouldReportInvalidCharacter() {
        Lexer lexer = new Lexer("123 @ 456");

        assertThrows(
                LexerException.class,
                lexer::tokenize
        );
    }

    @Test
    void shouldTrackTokenPosition() {
        Lexer lexer = new Lexer("123\n456");
        List<Token> tokens = lexer.tokenize();

        assertEquals(1, tokens.get(0).getLine());
        assertEquals(1, tokens.get(0).getColumn());

        assertEquals(2, tokens.get(1).getLine());
        assertEquals(1, tokens.get(1).getColumn());
    }


    @Test
    void shouldTokenizeCompleteAssignment() {
        Lexer lexer = new Lexer("let age = 20 + 1;");
        List<Token> tokens = lexer.tokenize();

        TokenType[] expected = {
                TokenType.LET,
                TokenType.IDENTIFIER,
                TokenType.ASSIGN,
                TokenType.INTEGER,
                TokenType.PLUS,
                TokenType.INTEGER,
                TokenType.SEMICOLON,
                TokenType.EOF
        };

        assertEquals(expected.length, tokens.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).getType());
        }
    }


}
