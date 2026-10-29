package grupp8.Interpreter;

import grupp8.Environment;

import java.util.HashMap;
import java.util.Map;

// Evaluates AST using visitor pattern.
public class Interpreter implements ASTVisitor<Object> {
    private final Environment runtimeEnvironment;
    private final Environment globalEnvironment;
    private final Map<String, FunctionNode> functions;

    public Interpreter(Environment runtimeEnvironment) {
        this(runtimeEnvironment, runtimeEnvironment, new HashMap<>());
    }

    private Interpreter(Environment runtimeEnvironment, Environment globalEnvironment,
                        Map<String, FunctionNode> functions) {
        this.runtimeEnvironment = runtimeEnvironment;
        this.globalEnvironment = globalEnvironment;
        this.functions = functions;
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
            return integerValue(leftVal) + integerValue(rightVal);
        }

        if (node.getOperatorSymbol() == '-') {
            int leftInt = integerValue(leftVal);
            int rightInt = integerValue(rightVal);
            return leftInt - rightInt;
        }

        throw new RuntimeException("Okänd operator: " + node.getOperatorSymbol());
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
                case "<" -> leftInt < rightInt;
                case ">" -> leftInt > rightInt;
                case "<=" -> leftInt <= rightInt;
                case ">=" -> leftInt >= rightInt;
                default -> throw new RuntimeException("Okänd jämförelseoperator: " + node.getComparisonOperator());
            };
        } else {
            String leftStr = String.valueOf(leftVal);
            String rightStr = String.valueOf(rightVal);
            int comparisonResult = leftStr.compareTo(rightStr);
            return switch (node.getComparisonOperator()) {
                case "==" -> comparisonResult == 0;
                case "!=" -> comparisonResult != 0;
                case "<" -> comparisonResult < 0;
                case ">" -> comparisonResult > 0;
                default -> throw new RuntimeException("Ogiltig strängjämförelse");
            };
        }
    }

    @Override
    public Object visit(LetNode node) {
        Object evaluatedValue = node.getExpressionNode().accept(this);
        runtimeEnvironment.defineVariable(node.getVariableName(), evaluatedValue);
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
        boolean conditionResult = evaluateCondition(node.getConditionNode());
        if (conditionResult) {
            node.getThenBranch().accept(this);
        } else if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileNode node) {
        while (evaluateCondition(node.getConditionNode())) {
            node.getLoopBody().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(FunctionNode node) {
        if (functions.containsKey(node.getName())) {
            throw new RuntimeException("Funktionen '" + node.getName() + "' är redan definierad");
        }
        functions.put(node.getName(), node);
        return null;
    }

    @Override
    public Object visit(CallNode node) {
        FunctionNode function = functions.get(node.getName());
        if (function == null) {
            throw new RuntimeException("Funktionen '" + node.getName() + "' är inte definierad");
        }
        if (node.getArguments().size() != function.getParameters().size()) {
            throw new RuntimeException("Funktionen '" + node.getName() + "' förväntar sig "
                    + function.getParameters().size() + " argument, men fick " + node.getArguments().size());
        }

        Environment localEnvironment = new Environment(globalEnvironment);
        for (int i = 0; i < node.getArguments().size(); i++) {
            Object value = node.getArguments().get(i).accept(this);
            localEnvironment.defineVariable(function.getParameters().get(i), value);
        }
        Interpreter functionInterpreter = new Interpreter(localEnvironment, globalEnvironment, functions);
        try {
            function.getBody().accept(functionInterpreter);
        } catch (ReturnValue result) {
            return result.value;
        }
        return null;
    }

    @Override
    public Object visit(ReturnNode node) {
        Object value = null;
        if (node.getExpression() != null) {
            value = node.getExpression().accept(this);
        }
        throw new ReturnValue(value);
    }

    private int integerValue(Object value) {
        if (value instanceof Integer integer) {
            return integer;
        }
        throw new RuntimeException("Beräkningen kräver heltal");
    }

    private boolean evaluateCondition(ASTNode condition) {
        Object value = condition.accept(this);
        if (value instanceof Boolean result) {
            return result;
        }
        throw new RuntimeException("Villkoret måste ge sant eller falskt");
    }

    private static class ReturnValue extends RuntimeException {
        private final Object value;

        private ReturnValue(Object value) {
            this.value = value;
        }
    }
}
