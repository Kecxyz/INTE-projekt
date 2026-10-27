package se.ariful;

// Evaluates AST using visitor pattern.
public class Interpreter implements ASTVisitor<Object> {
    private final Environment runtimeEnvironment;

    public Interpreter(Environment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public Object interpret(ASTNode rootNode) {
        return rootNode.accept(this);
    }

    @Override
    public Object visit(VarNode node) {
        return runtimeEnvironment.getVariable(node.getVariableName());
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
            int leftInt = (Integer) leftVal;
            int rightInt = (Integer) rightVal;
            return leftInt - rightInt;
        }

        throw new RuntimeException("Unknown operator: " + node.getOperatorSymbol());
    }

    @Override
    public Object visit(CompareNode node) {
        Object leftVal = node.getLeftOperand().accept(this);
        Object rightVal = node.getRightOperand().accept(this);

        if (leftVal instanceof Integer && rightVal instanceof Integer) {
            int leftInt = (Integer) leftVal;
            int rightInt = (Integer) rightVal;
            return switch (node.getComparisonOperator()) {
                case "==" -> leftInt == rightInt;
                case "!=" -> leftInt != rightInt;
                case "<"  -> leftInt < rightInt;
                case ">"  -> leftInt > rightInt;
                case "<=" -> leftInt <= rightInt;
                case ">=" -> leftInt >= rightInt;
                default -> throw new RuntimeException("Unknown comparison: " + node.getComparisonOperator());
            };
        } else {
            String leftStr = String.valueOf(leftVal);
            String rightStr = String.valueOf(rightVal);
            int comparisonResult = leftStr.compareTo(rightStr);
            return switch (node.getComparisonOperator()) {
                case "==" -> comparisonResult == 0;
                case "!=" -> comparisonResult != 0;
                case "<"  -> comparisonResult < 0;
                case ">"  -> comparisonResult > 0;
                default -> throw new RuntimeException("Invalid string comparison");
            };
        }
    }

    @Override
    public Object visit(LetNode node) {
        Object evaluatedValue = node.getExpressionNode().accept(this);
        runtimeEnvironment.setVariable(node.getVariableName(), evaluatedValue);
        return evaluatedValue;
    }

    @Override
    public Object visit(AssignNode node) {
        Object evaluatedValue = node.getExpressionNode().accept(this);
        runtimeEnvironment.setVariable(node.getVariableName(), evaluatedValue);
        return evaluatedValue;
    }

    @Override
    public Object visit(PrintNode node) {
        Object evaluatedValue = node.getExpressionNode().accept(this);
        runtimeEnvironment.printOutput(evaluatedValue);
        return null;
    }

    @Override
    public Object visit(BlockNode node) {
        for (ASTNode statement : node.getStatementList()) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(IfNode node) {
        boolean conditionResult = (Boolean) node.getConditionNode().accept(this);
        if (conditionResult) {
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