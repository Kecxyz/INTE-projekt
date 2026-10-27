package se.ariful;

import java.util.List;

// Executes sequential statement block list.
public class BlockNode implements ASTNode {
    private final List<ASTNode> statementList;

    public BlockNode(List<ASTNode> statementList) {
        this.statementList = statementList;
    }

    public List<ASTNode> getStatementList() {
        return statementList;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}