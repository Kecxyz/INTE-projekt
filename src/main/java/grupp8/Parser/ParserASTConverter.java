package grupp8.Parser;

import grupp8.Interpreter.*;
import grupp8.Lexer.TokenType;

public final class ParserASTConverter {
    private ParserASTConverter() {}

    public static ASTNode toAST(ParserExpression expression) {
        if(expression instanceof  ParserExpression.IntegerLiteral literal) {
            return new IntegerLiteralNode(literal.value());
        }

        if(expression instanceof  ParserExpression.StringLiteral literal) {
            return new StringLiteralNode(literal.value());
        }

        if(expression instanceof  ParserExpression.VariableExpression variable) {
            return new VarNode(variable.name());
        }

        if (expression instanceof ParserExpression.UnaryExpression unary) {
            if (unary.operator() == TokenType.MINUS) {
                // -x representeras som 0 - x.
                return new BinaryOpNode(
                        new IntegerLiteralNode(0),
                        '-',
                        toAST(unary.operand())
                );
            }

            throw new IllegalArgumentException(
                    "Operatorn stöds inte: " + unary.operator()
            );
        }

        if (expression instanceof ParserExpression.BinaryExpression binary) {
            ASTNode left = toAST(binary.left());
            ASTNode right = toAST(binary.right());

            return switch (binary.operator()) {
                case PLUS -> new BinaryOpNode(left, '+', right);
                case MINUS -> new BinaryOpNode(left, '-', right);
                case EQUAL_EQUAL -> new CompareNode(left, "==", right);
                case NOT_EQUAL -> new CompareNode(left, "!=", right);
                case LESS_THAN -> new CompareNode(left, "<", right);
                case GREATER_THAN -> new CompareNode(left, ">", right);
                case LESS_EQUAL -> new CompareNode(left, "<=", right);
                case GREATER_EQUAL -> new CompareNode(left, ">=", right);

                default -> throw new IllegalArgumentException(
                        "Operatorn stöds inte: " + binary.operator()
                );
            };
        }

        throw new IllegalArgumentException("Okänd uttryckstyp");
    }
}

