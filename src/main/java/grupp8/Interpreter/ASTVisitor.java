package grupp8.Interpreter;

// Interface for traversing AST nodes.
public interface ASTVisitor<T> {
    T visit(VarNode node);
    T visit(IntegerLiteralNode node);
    T visit(StringLiteralNode node);
    T visit(BinaryOpNode node);
    T visit(CompareNode node);
    T visit(LetNode node);
    T visit(AssignNode node);
    T visit(PrintNode node);
    T visit(BlockNode node);
    T visit(IfNode node);
    T visit(WhileNode node);
    T visit(FunctionNode node);
    T visit(CallNode node);
    T visit(ReturnNode node);
}
