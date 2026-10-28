package grupp8.Interpreter;

import java.util.List;

public class CallNode implements ASTNode {
    private final String name;
    private final List<ASTNode> arguments;

    public CallNode(String name, List<ASTNode> arguments) {
        this.name = name;
        this.arguments = List.copyOf(arguments);
    }

    public String getName() {
        return name;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
