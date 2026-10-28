package grupp8.Interpreter;

import java.util.List;

public class FunctionNode implements ASTNode {
    private final String name;
    private final List<String> parameters;
    private final BlockNode body;

    public FunctionNode(String name, List<String> parameters, BlockNode body) {
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public BlockNode getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
