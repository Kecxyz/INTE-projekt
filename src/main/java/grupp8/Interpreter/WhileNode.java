package grupp8.Interpreter;

// Loops execution while condition holds.
public class WhileNode implements ASTNode {
    private final ASTNode conditionNode;
    private final ASTNode loopBody;

    public WhileNode(ASTNode conditionNode, ASTNode loopBody) {
        this.conditionNode = conditionNode;
        this.loopBody = loopBody;
    }

    public ASTNode getConditionNode() { return conditionNode; }
    public ASTNode getLoopBody() { return loopBody; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}