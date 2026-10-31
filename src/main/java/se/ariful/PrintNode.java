package se.ariful;

// Outputs expression results to console.
public class PrintNode implements ASTNode {
    private final ASTNode expressionNode;

    public PrintNode(ASTNode expressionNode) {
        this.expressionNode = expressionNode;
    }

    public ASTNode getExpressionNode() {
        return expressionNode;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}