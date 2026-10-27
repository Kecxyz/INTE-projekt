package se.ariful;

public class Interpreter {

    private final Environment env;

    public Interpreter(Environment env) {
        this.env = env;
    }

    // Main entry point for evaluating a ParserExpression tree
    public Object interpret(ParserExpression expr) {
        return evaluate(expr);
    }

    private Object evaluate(ParserExpression expr) {
        if (expr instanceof ParserExpression.IntegerLiteral lit) {
            return lit.value();
        }

        if (expr instanceof ParserExpression.StringLiteral lit) {
            return lit.value();
        }

        if (expr instanceof ParserExpression.VariableExpression varExpr) {
            return env.getVariable(varExpr.name());
        }

        if (expr instanceof ParserExpression.UnaryExpression unary) {
            Object operandValue = evaluate(unary.operand());
            if (unary.operator() == TokenType.MINUS) {
                if (operandValue instanceof Integer i) {
                    return -i;
                }
            }
            throw new RuntimeException("Unsupported unary operation");
        }

        if (expr instanceof ParserExpression.BinaryExpression bin) {
            Object left = evaluate(bin.left());
            Object right = evaluate(bin.right());

            TokenType op = bin.operator();

            // Handle Addition / Concatenation
            if (op == TokenType.PLUS) {
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l + r;
                }
            }

            // Handle Subtraction
            if (op == TokenType.MINUS) {
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l - r;
                }
            }

            // Handle Comparisons (<, >, etc.)
            if (left instanceof Integer l && right instanceof Integer r) {
                return switch (op) {
                    case LESS_THAN -> l < r;
                    case GREATER_THAN -> l > r;
                    case LESS_EQUAL -> l <= r;
                    case GREATER_EQUAL -> l >= r;
                    default -> throw new RuntimeException("Unknown operator: " + op);
                };
            }

            throw new RuntimeException("Unsupported binary operation types");
        }

        throw new RuntimeException("Unknown expression type");
    }
}