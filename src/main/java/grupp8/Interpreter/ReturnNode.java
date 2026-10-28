package grupp8.Interpreter;

public class ReturnNode implements ASTNode {
    private final ASTNode expression;

    public ReturnNode(ASTNode expression) {
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
