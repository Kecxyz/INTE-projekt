package grupp8.Interpreter;

// Base interface for syntax nodes.
public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}