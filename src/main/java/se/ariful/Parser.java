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
}
