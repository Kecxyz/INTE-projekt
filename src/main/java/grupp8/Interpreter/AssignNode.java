package grupp8.Interpreter;

// Updates existing variable memory value.
public class AssignNode implements ASTNode {
    private final String variableName;
    private final ASTNode expressionNode;

    public AssignNode(String variableName, ASTNode expressionNode) {
        this.variableName = variableName;
        this.expressionNode = expressionNode;
    }

    public String getVariableName() { return variableName; }
    public ASTNode getExpressionNode() { return expressionNode; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}