package grupp8.Interpreter;

// Handles conditional branch control flow.
public class IfNode implements ASTNode {
    private final ASTNode conditionNode;
    private final ASTNode thenBranch;
    private final ASTNode elseBranch;

    public IfNode(ASTNode conditionNode, ASTNode thenBranch, ASTNode elseBranch) {
        this.conditionNode = conditionNode;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ASTNode getConditionNode() { return conditionNode; }
    public ASTNode getThenBranch() { return thenBranch; }
    public ASTNode getElseBranch() { return elseBranch; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}