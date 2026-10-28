package grupp8.Interpreter;

// Holds constant string literal value.
public class StringLiteralNode implements ASTNode {
    private final String literalValue;

    public StringLiteralNode(String literalValue) {
        this.literalValue = literalValue;
    }

    public String getLiteralValue() {
        return literalValue;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}