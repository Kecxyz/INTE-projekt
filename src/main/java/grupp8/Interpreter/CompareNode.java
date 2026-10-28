package grupp8.Interpreter;

// Evaluates relational comparison operators.
public class CompareNode implements ASTNode {
    private final ASTNode leftOperand;
    private final ASTNode rightOperand;
    private final String comparisonOperator;

    public CompareNode(ASTNode leftOperand, String comparisonOperator, ASTNode rightOperand) {
        this.leftOperand = leftOperand;
        this.comparisonOperator = comparisonOperator;
        this.rightOperand = rightOperand;
    }

    public ASTNode getLeftOperand() { return leftOperand; }
    public ASTNode getRightOperand() { return rightOperand; }
    public String getComparisonOperator() { return comparisonOperator; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}