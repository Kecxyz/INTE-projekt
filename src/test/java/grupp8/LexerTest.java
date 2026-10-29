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
    void recognizeFunctionNameWithUnderscore() {
        List<Token> tokens = new Lexer("func_var1").tokenize();
        assertEquals("func_var1", tokens.getFirst().value());
    }

    @Test
    void tokenToStringTest() {
        List<Token> tokens = new Lexer("return").tokenize();

        assertEquals("RETURN(return)at1:1", tokens.getFirst().toString());
    }

    @Test
    void shouldRecognizeFunctionKeywordsAndComma() {
        List<Token> tokens = new Lexer("function return ,").tokenize();

        assertEquals(TokenType.FUNCTION, tokens.get(0).type());
        assertEquals(TokenType.RETURN, tokens.get(1).type());
        assertEquals(TokenType.COMMA, tokens.get(2).type());
        assertEquals(TokenType.EOF, tokens.get(3).type());
    }

    @Test
    void shouldTokenizeInteger() {
        Lexer lexer = new Lexer("123");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).type());
        assertEquals("123", tokens.get(0).value());
        assertEquals(TokenType.EOF, tokens.get(1).type());
    }

    @Test
    void shouldTokenizeAddition() {
        Lexer lexer = new Lexer("12 + 34");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).type());
        assertEquals("12", tokens.get(0).value());

        assertEquals(TokenType.PLUS, tokens.get(1).type());

        assertEquals(TokenType.INTEGER, tokens.get(2).type());
        assertEquals("34", tokens.get(2).value());
    }

    @Test
    void shouldTokenizeSubtraction() {
        Lexer lexer = new Lexer("10 - 3");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).type());
        assertEquals(TokenType.MINUS, tokens.get(1).type());
        assertEquals(TokenType.INTEGER, tokens.get(2).type());
    }

    @Test
    void shouldSkipWhitespace() {
        Lexer lexer = new Lexer("   123   +   4   ");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.INTEGER, tokens.get(0).type());
        assertEquals(TokenType.PLUS, tokens.get(1).type());
        assertEquals(TokenType.INTEGER, tokens.get(2).type());
        assertEquals(TokenType.EOF, tokens.get(3).type());
    }


    @Test
    void shouldRecognizeKeywords() {
        Lexer lexer = new Lexer("let if else while print");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LET, tokens.get(0).type());
        assertEquals(TokenType.IF, tokens.get(1).type());
        assertEquals(TokenType.ELSE, tokens.get(2).type());
        assertEquals(TokenType.WHILE, tokens.get(3).type());
        assertEquals(TokenType.PRINT, tokens.get(4).type());
    }

    @Test
    void shouldRecognizeIdentifier() {
        Lexer lexer = new Lexer("myVariable");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.getFirst().type());
        assertEquals("myVariable", tokens.getFirst().value());
    }

    @Test
    void keywordInsideIdentifierShouldRemainIdentifier() {
        Lexer lexer = new Lexer("letter");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.getFirst().type());
        assertEquals("letter", tokens.getFirst().value());
    }


    @Test
    void shouldTokenizeString() {
        Lexer lexer = new Lexer("\"Hello world\"");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.STRING, tokens.getFirst().type());
        assertEquals("Hello world", tokens.getFirst().value());
    }

    @Test
    void shouldTokenizeAssignmentAndSemicolon() {
        Lexer lexer = new Lexer("let x = 5;");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LET, tokens.get(0).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals(TokenType.ASSIGN, tokens.get(2).type());
        assertEquals(TokenType.INTEGER, tokens.get(3).type());
        assertEquals(TokenType.SEMICOLON, tokens.get(4).type());
    }


    @Test
    void shouldTokenizeComparisonOperators() {
        Lexer lexer = new Lexer("== != < > <= >=");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(0).type());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(1).type());
        assertEquals(TokenType.LESS_THAN, tokens.get(2).type());
        assertEquals(TokenType.GREATER_THAN, tokens.get(3).type());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(4).type());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(5).type());
    }

    @Test
    void shouldTokenizeBracesAndParentheses() {
        Lexer lexer = new Lexer("(x) { }");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LEFT_PAREN, tokens.get(0).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(2).type());
        assertEquals(TokenType.LEFT_BRACE, tokens.get(3).type());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(4).type());
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

        assertEquals(1, tokens.get(0).line());
        assertEquals(1, tokens.get(0).column());

        assertEquals(2, tokens.get(1).line());
        assertEquals(1, tokens.get(1).column());
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
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void shouldRejectIntegerFollowedByLetter() {
        Lexer lexer = new Lexer("let a = 23e");

        assertThrows(
                LexerException.class,
                lexer::tokenize
        );
    }
}
