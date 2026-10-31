package se.ariful;

// Resolves variable value from memory.
public class VarNode implements ASTNode {
    private final String variableName;

    public VarNode(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}