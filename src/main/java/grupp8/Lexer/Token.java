package grupp8.Lexer;

public record Token(TokenType type, String value, int line, int column) {

    @Override
    public String toString() {
        return type + "(" + value + ")" + "at" + line + ":" + column;
    }
}
