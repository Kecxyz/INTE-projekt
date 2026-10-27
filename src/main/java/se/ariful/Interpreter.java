package se.ariful;

public class Interpreter {

    private final Environment env;

    public Interpreter(Environment env) {
        this.env = env;
    }

    public Object interpret(ParserExpression expr) {
        return evaluate(expr);
    }

    private Object evaluate(ParserExpression expr) {
        // 1. Integer Literals (e.g., 42)
        if (expr instanceof ParserExpression.IntegerLiteral lit) {
            return lit.value();
        }

        // 2. String Literals (e.g., "Hello")
        if (expr instanceof ParserExpression.StringLiteral lit) {
            return lit.value();
        }

        // 3. Variables (e.g., x)
        if (expr instanceof ParserExpression.VariableExpression varExpr) {
            return env.getVariable(varExpr.name());
        }

        // 4. Unary Expressions (e.g., -5)
        if (expr instanceof ParserExpression.UnaryExpression unary) {
            Object operandValue = evaluate(unary.operand());
            if (unary.operator() == TokenType.MINUS && operandValue instanceof Integer i) {
                return -i;
            }
            throw new RuntimeException("Unsupported unary operation");
        }

        // 5. Binary Expressions (e.g., 10 + 20, x < 5, etc.)
        if (expr instanceof ParserExpression.BinaryExpression bin) {
            Object left = evaluate(bin.left());
            Object right = evaluate(bin.right());
            TokenType op = bin.operator();

            // Addition & String Concatenation
            if (op == TokenType.PLUS) {
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l + r;
                }
            }

            // Subtraction
            if (op == TokenType.MINUS) {
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l - r;
                }
            }

            // Equality / Inequality
            if (op == TokenType.EQUAL_EQUAL) {
                return left.equals(right);
            }
            if (op == TokenType.NOT_EQUAL) {
                return !left.equals(right);
            }

            // Numeric Comparisons
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