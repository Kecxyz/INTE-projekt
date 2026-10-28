package grupp8.Lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;

    private int position = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return position >= source.length();
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(position);
    }

    private void advance() {
        char current = source.charAt(position);
        position++;

        if (current == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }

    }

    private Token readInteger() {
        int start = position;
        int startLine = line;
        int startColumn = column;

        while (Character.isDigit(peek())) {
            advance();
        }

        if (Character.isLetter(peek()) || peek() == '_') {
            throw new LexerException("Invalid number at" + startLine + ":" + startColumn);
        }

        String value = source.substring(start, position);

        return new Token(TokenType.INTEGER, value, startLine, startColumn);
    }

    private Token readIdentifier() {
        int start = position;
        int startLine = line;
        int startColumn = column;

        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            advance();
        }

        String value = source.substring(start, position);

        TokenType type = switch (value) {
            case "let" -> TokenType.LET;
            case "if" -> TokenType.IF;
            case "else" -> TokenType.ELSE;
            case "while" -> TokenType.WHILE;
            case "print" -> TokenType.PRINT;
            case "function" -> TokenType.FUNCTION;
            case "return" -> TokenType.RETURN;
            default -> TokenType.IDENTIFIER;
        };

        return new Token(type, value, startLine, startColumn);
    }

    private Token readString() {
        advance();
        int start = position;
        int startLine = line;
        int startColumn = column;

        while (peek() != '"') {
            advance();
        }

        if (isAtEnd()) {
            throw new LexerException("Strängen är inte avslutad vid " + startLine + ":" + startColumn);
        }
        String value = source.substring(start, position);

        advance();

        return new Token(TokenType.STRING, value, startLine, startColumn);
    }

    private boolean matchEqualSign() {
        if (peek() != '=') {
            return false;
        }
        advance();
        return true;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (!isAtEnd()) {
            char current = peek();

            if (Character.isWhitespace(current)) {
                advance();
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(readInteger());
                continue;
            }

            if (Character.isLetter(current) || current == '_') {
                tokens.add(readIdentifier());
                continue;
            }

            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            int tokenLine = line;
            int tokenColumn = column;

            advance();
            switch (current) {
                case '+' -> tokens.add(new Token(TokenType.PLUS, "+", tokenLine, tokenColumn));
                case '-' -> tokens.add(new Token(TokenType.MINUS, "-", tokenLine, tokenColumn));
                case ';' -> tokens.add(new Token(TokenType.SEMICOLON, ";", tokenLine, tokenColumn));
                case ',' -> tokens.add(new Token(TokenType.COMMA, ",", tokenLine, tokenColumn));
                case '(' -> tokens.add(new Token(TokenType.LEFT_PAREN, "(", tokenLine, tokenColumn));
                case ')' -> tokens.add(new Token(TokenType.RIGHT_PAREN, ")", tokenLine, tokenColumn));
                case '{' -> tokens.add(new Token(TokenType.LEFT_BRACE, "{", tokenLine, tokenColumn));
                case '}' -> tokens.add(new Token(TokenType.RIGHT_BRACE, "}", tokenLine, tokenColumn));
                case '=' -> {
                    if (matchEqualSign()) {
                        tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", tokenLine, tokenColumn));
                    } else {
                        tokens.add(new Token(TokenType.ASSIGN, "=", tokenLine, tokenColumn));
                    }
                }
                case '<' -> {
                    if (matchEqualSign()) {
                        tokens.add(new Token(TokenType.LESS_EQUAL, "<=", tokenLine, tokenColumn));
                    } else {
                        tokens.add(new Token(TokenType.LESS_THAN, "<", tokenLine, tokenColumn));
                    }
                }
                case '>' -> {
                    if (matchEqualSign()) {
                        tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", tokenLine, tokenColumn));
                    } else {
                        tokens.add(new Token(TokenType.GREATER_THAN, ">", tokenLine, tokenColumn));
                    }
                }
                case '!' -> {
                    if (matchEqualSign()) {
                        tokens.add(new Token(TokenType.NOT_EQUAL, "!=", tokenLine, tokenColumn));
                    } else {
                        throw new LexerException("Oväntat tecken '!' vid " + tokenLine + ":" + tokenColumn);
                    }
                }
                default ->
                        throw new LexerException("Oväntat tecken '" + current + "' vid " + tokenLine + ":" + tokenColumn);
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
}
