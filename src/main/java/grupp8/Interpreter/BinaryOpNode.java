package grupp8.Interpreter;

// Computes arithmetic and string concat.
public class BinaryOpNode implements ASTNode {
    private final ASTNode leftOperand;
    private final ASTNode rightOperand;
    private final char operatorSymbol;

    public BinaryOpNode(ASTNode leftOperand, char operatorSymbol, ASTNode rightOperand) {
        this.leftOperand = leftOperand;
        this.operatorSymbol = operatorSymbol;
        this.rightOperand = rightOperand;
    }

    public ASTNode getLeftOperand() {
        return leftOperand;
    }

    public ASTNode getRightOperand() {
        return rightOperand;
    }

    public char getOperatorSymbol() {
        return operatorSymbol;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}