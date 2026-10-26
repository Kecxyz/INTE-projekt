package se.ariful;

// Base interface for syntax nodes.
public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}