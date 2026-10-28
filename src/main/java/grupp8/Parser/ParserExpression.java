package grupp8.Parser;

import grupp8.Lexer.TokenType;

import java.util.List;

public interface ParserExpression {
    record CallExpression(String name, List<ParserExpression> arguments) implements ParserExpression {
    }

    record IntegerLiteral(int value) implements ParserExpression {

    }

    record BinaryExpression(ParserExpression left, TokenType operator,
                            ParserExpression right) implements ParserExpression {

    }

    record StringLiteral(String value) implements ParserExpression {
    }

    record VariableExpression(String name) implements ParserExpression {

    }

    record UnaryExpression(TokenType operator, ParserExpression operand) implements ParserExpression {

    }

}
