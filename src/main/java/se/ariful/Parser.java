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

        if(token.getType() == TokenType.INTEGER) {
            int value = Integer.parseInt(token.getValue());
            advance();

            return new ParserExpression.IntegerLiteral(value);
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
            "Förväntqade ett heltal vid "
                + token.getLine() + ":" + token.getColumn() + ", fick " + token.getType()
        );

    }

    public ParserExpression parseExpression() {
        ParserExpression expression = parseUnary();

        while(peek().getType() == TokenType.PLUS || peek().getType() == TokenType.MINUS) {
            Token operator = advance();
            ParserExpression right = parseUnary();

            expression = new ParserExpression.BinaryExpression(expression, operator.getType(), right);
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
