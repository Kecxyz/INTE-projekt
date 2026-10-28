package grupp8.Interpreter;

// Declares and initializes new variables.
public class LetNode implements ASTNode {
    private final String variableName;
    private final ASTNode expressionNode;

    public LetNode(String variableName, ASTNode expressionNode) {
        this.variableName = variableName;
        this.expressionNode = expressionNode;
    }

    public String getVariableName() { return variableName; }
    public ASTNode getExpressionNode() { return expressionNode; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}