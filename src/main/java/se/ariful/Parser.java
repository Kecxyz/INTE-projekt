package se.ariful;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = List.copyOf(tokens);
    }

    private  ParserExpression parserPrimary() {
        Token token = tokens.get(current);

        if(token.getType() == TokenType.INTEGER) {
            int value = Integer.parseInt(token.getValue());
            current++;

            return new ParserExpression.IntegerLiteral(value);
        }

        throw new ParserException(
            "Förväntqade ett heltal vid "
                + token.getLine() + ":" + token.getColumn() + ", fick " + token.getType()
        );

    }

    public ParserExpression parseExpression() {
        ParserExpression expression = parserPrimary();

        while(tokens.get(current).getType() == TokenType.PLUS) {
            Token operator = tokens.get(current);
            current++;

            ParserExpression right = parserPrimary();

            expression = new ParserExpression.BinaryExpression(expression, operator.getType(), right);
        }
        
        return expression;
    }
        
}
