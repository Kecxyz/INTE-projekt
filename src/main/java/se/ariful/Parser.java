package se.ariful;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = List.copyOf(tokens);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        Token token = peek();
        current++;
        return token;
    }

    private  ParserExpression parserPrimary() {
        Token token = peek();

        if (token.getType() == TokenType.INTEGER) {
            try {
                int value = Integer.parseInt(token.getValue());
                advance();

                return new ParserExpression.IntegerLiteral(value);
            } catch (NumberFormatException exception) {
                throw new ParserException(
                        "Ogiltigt heltal '" + token.getValue() + "' vid "
                                + token.getLine() + ":" + token.getColumn()
                );
            }
        }

        if(token.getType() == TokenType.STRING) {
            advance();

            return new ParserExpression.StringLiteral(token.getValue());
        }

        if(token.getType() == TokenType.IDENTIFIER) {
            advance();

            return new ParserExpression.VariableExpression(token.getValue());
        }

        if(token.getType() == TokenType.LEFT_PAREN) {
            advance();

            ParserExpression expression = parseExpression();

            consume(TokenType.RIGHT_PAREN, "Förväntade ')'");

            return expression;
        }

        throw new ParserException(
            "Förväntqade ett uttryck vid "
                + token.getLine() + ":" + token.getColumn() + ", fick " + token.getType()
        );

    }

    public ParserExpression parseExpression() {
        return parseEquality();
    }


    public void parseProgram() {
        while (peek().getType() != TokenType.EOF) {
            parseStatement();
        }

        consume(
                TokenType.EOF,
                "Förväntade slut på programmet"
        );
    }

    public void parseStatement() {
        if (peek().getType() == TokenType.LET) {
            parseLetStatement();
            return;
        }

        if (peek().getType() == TokenType.IDENTIFIER) {
            parseAssignmentStatement();
            return;
        }

        if (peek().getType() == TokenType.PRINT) {
            parsePrintStatement();
            return;
        }

        if (peek().getType() == TokenType.LEFT_BRACE) {
            parseBlock();
            return;
        }

        if (peek().getType() == TokenType.IF) {
            parseIfStatement();
            return;
        }

        if (peek().getType() == TokenType.WHILE) {
            parseWhileStatement();
            return;
        }

        Token token = peek();

        throw new ParserException(
                "Förväntade en instruktion vid "
                        + token.getLine() + ":" + token.getColumn()
                        + ", fick " + token.getType()
        );
    }

    private void parseLetStatement() {
        consume(TokenType.LET, "Förväntade 'let'");

        consume(
                TokenType.IDENTIFIER,
                "Förväntade ett variabelnamn efter 'let'"
        );

        consume(TokenType.ASSIGN, "Förväntade '=' efter variabelnamnet");

        parseExpression();

        consume(TokenType.SEMICOLON, "Förväntade ';' efter deklarationen");
    }

    private void parseAssignmentStatement() {
        consume(TokenType.IDENTIFIER, "Förväntade ett variabelnamn");

        consume(TokenType.ASSIGN, "Förväntade '=' efter variabelnamnet");

        parseExpression();

        consume(TokenType.SEMICOLON, "Förväntade ';' efter tilldelningen");
    }

    private void parseIfStatement() {
        consume(TokenType.IF, "Förväntade 'if'");

        consume(
                TokenType.LEFT_PAREN,
                "Förväntade '(' efter 'if'"
        );

        parseExpression();

        consume(
                TokenType.RIGHT_PAREN,
                "Förväntade ')' efter villkoret"
        );

        parseBlock();

        if (peek().getType() == TokenType.ELSE) {
            advance();
            parseBlock();
        }
    }

    private void parseWhileStatement() {
        consume(TokenType.WHILE, "Förväntade 'while'");

        consume(
                TokenType.LEFT_PAREN,
                "Förväntade '(' efter 'while'"
        );

        parseExpression();

        consume(
                TokenType.RIGHT_PAREN,
                "Förväntade ')' efter villkoret"
        );

        parseBlock();
    }

    private void parsePrintStatement() {
        consume(TokenType.PRINT, "Förväntade 'print'");

        consume(
                TokenType.LEFT_PAREN,
                "Förväntade '(' efter 'print'"
        );

        parseExpression();

        consume(
                TokenType.RIGHT_PAREN,
                "Förväntade ')' efter utskriftsuttrycket"
        );

        consume(
                TokenType.SEMICOLON,
                "Förväntade ';' efter utskriften"
        );
    }

    private void parseBlock() {
        consume(TokenType.LEFT_BRACE, "Förväntade '{'");

        while (peek().getType() != TokenType.RIGHT_BRACE
                && peek().getType() != TokenType.EOF) {

            parseStatement();
        }

        consume(
                TokenType.RIGHT_BRACE,
                "Förväntade '}' efter kodblocket"
        );
    }

    public ParserExpression parseCompleteExpression() {
        ParserExpression expression = parseExpression();

        consume(
            TokenType.EOF,
            "Förväntade slut på inmatningen"
        );

        return expression;
    }

    private ParserExpression parseComparison() {
        ParserExpression expression = parseAddition();

        while (peek().getType() == TokenType.LESS_THAN
                || peek().getType() == TokenType.GREATER_THAN
                || peek().getType() == TokenType.LESS_EQUAL
                || peek().getType() == TokenType.GREATER_EQUAL) {

            Token operator = advance();
            ParserExpression right = parseAddition();

            expression = new ParserExpression.BinaryExpression(
                    expression,
                    operator.getType(),
                    right
            );
        }

        return expression;
    }

    private ParserExpression parseEquality() {
    ParserExpression expression = parseComparison();

        while (peek().getType() == TokenType.EQUAL_EQUAL
                || peek().getType() == TokenType.NOT_EQUAL) {

            Token operator = advance();
            ParserExpression right = parseComparison();

            expression = new ParserExpression.BinaryExpression(
                    expression,
                    operator.getType(),
                    right
            );
        }

        return expression;
    }

    private ParserExpression parseAddition() {
        ParserExpression expression = parseUnary();

        while (peek().getType() == TokenType.PLUS
                || peek().getType() == TokenType.MINUS) {

            Token operator = advance();
            ParserExpression right = parseUnary();

            expression = new ParserExpression.BinaryExpression(
                    expression,
                    operator.getType(),
                    right
            );
        }

        return expression;
    }

    private Token consume(TokenType expected, String meddelande) {
        Token token = peek();

        if(token.getType() == expected) {
            return advance();
        }

        throw new ParserException(
            meddelande + "vid "
                + token.getLine() + ":" + token.getColumn()
                + ", fick " + token.getType()
        );
    }
    
    private ParserExpression parseUnary() {
        if(peek().getType() == TokenType.MINUS) {
            Token operator = advance();
            ParserExpression operand = parseUnary();

            return new ParserExpression.UnaryExpression(
                operator.getType(), operand
            );
        }
        return parserPrimary();
    }
        
}
