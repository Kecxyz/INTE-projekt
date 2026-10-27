package se.ariful;

public class Interpreter implements ASTVisitor<Object> {

    private final Environment env;

    public Interpreter(Environment env) {
        this.env = env;
    }

    // 1. Entry point for your ASTNode Visitor pattern (used by InterpreterTest)
    public Object interpret(ASTNode rootNode) {
        return rootNode.accept(this);
    }

    // 2. Entry point for your teammate's ParserExpression records
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
            if (unary.operator() == TokenType.MINUS && operandValue instanceof Integer i) {
                return -i;
            }
            throw new RuntimeException("Unsupported unary operation");
        }

        if (expr instanceof ParserExpression.BinaryExpression bin) {
            Object left = evaluate(bin.left());
            Object right = evaluate(bin.right());

            TokenType op = bin.operator();

            if (op == TokenType.PLUS) {
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l + r;
                }
            }

            if (op == TokenType.MINUS) {
                if (left instanceof Integer l && right instanceof Integer r) {
                    return l - r;
                }
            }

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

    // --- Visitor Pattern Methods (for your ASTNode tests) ---

    @Override
    public Object visit(VarNode node) {
        return env.getVariable(node.getVariableName());
    }

    @Override
    public Object visit(IntegerLiteralNode node) {
        return node.getLiteralValue();
    }

    @Override
    public Object visit(StringLiteralNode node) {
        return node.getLiteralValue();
    }

    @Override
    public Object visit(BinaryOpNode node) {
        Object leftVal = node.getLeftOperand().accept(this);
        Object rightVal = node.getRightOperand().accept(this);

        if (node.getOperatorSymbol() == '+') {
            if (leftVal instanceof String || rightVal instanceof String) {
                return String.valueOf(leftVal) + String.valueOf(rightVal);
            }
            return (Integer) leftVal + (Integer) rightVal;
        }

        if (node.getOperatorSymbol() == '-') {
            return (Integer) leftVal - (Integer) rightVal;
        }

        throw new RuntimeException("Unknown operator: " + node.getOperatorSymbol());
    }

    @Override
    public Object visit(CompareNode node) {
        Object leftVal = node.getLeftOperand().accept(this);
        Object rightVal = node.getRightOperand().accept(this);

        if (leftVal instanceof Integer && rightVal instanceof Integer) {
            int l = (Integer) leftVal;
            int r = (Integer) rightVal;
            return switch (node.getComparisonOperator()) {
                case "==" -> l == r;
                case "!=" -> l != r;
                case "<"  -> l < r;
                case ">"  -> l > r;
                case "<=" -> l <= r;
                case ">=" -> l >= r;
                default -> throw new RuntimeException("Unknown comparison: " + node.getComparisonOperator());
            };
        }
        throw new RuntimeException("Comparison requires integer types");
    }

    @Override
    public Object visit(LetNode node) {
        Object val = node.getExpressionNode().accept(this);
        env.setVariable(node.getVariableName(), val);
        return val;
    }

    @Override
    public Object visit(AssignNode node) {
        Object val = node.getExpressionNode().accept(this);
        env.setVariable(node.getVariableName(), val);
        return val;
    }

    @Override
    public Object visit(PrintNode node) {
        Object val = node.getExpressionNode().accept(this);
        env.printOutput(val);
        return null;
    }

    @Override
    public Object visit(BlockNode node) {
        for (ASTNode stmt : node.getStatementList()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(IfNode node) {
        boolean cond = (Boolean) node.getConditionNode().accept(this);
        if (cond) {
            node.getThenBranch().accept(this);
        } else if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileNode node) {
        while ((Boolean) node.getConditionNode().accept(this)) {
            node.getLoopBody().accept(this);
        }
        return null;
    }
}