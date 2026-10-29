package grupp8.Interpreter;

// Holds constant integer literal value.
public class IntegerLiteralNode implements ASTNode {
    private final int literalValue;

    public IntegerLiteralNode(int literalValue) {
        this.literalValue = literalValue;
    }

    public int getLiteralValue() {
        return literalValue;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}