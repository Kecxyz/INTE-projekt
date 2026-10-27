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

        throw new ParserException(
            "Förväntqade ett heltal vid "
                + token.getLine() + ":" + token.getColumn() + ", fick " + token.getType()
        );

    }

    public ParserExpression parseExpression() {
        ParserExpression expression = parserPrimary();

        while(peek().getType() == TokenType.PLUS || peek().getType() == TokenType.MINUS) {
            Token operator = advance();
            ParserExpression right = parserPrimary();

            expression = new ParserExpression.BinaryExpression(expression, operator.getType(), right);
        }
        
        return expression;
    }
        
}
