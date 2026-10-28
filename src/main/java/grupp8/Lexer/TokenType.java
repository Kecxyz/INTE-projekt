package grupp8.Lexer;

public enum TokenType {
    LET("'let'"),
    IF("'if'"),
    ELSE("'else'"),
    WHILE("'while'"),
    PRINT("'print'"),
    FUNCTION("'function'"),
    RETURN("'return'"),

    IDENTIFIER("ett variabel- eller funktionsnamn"),
    INTEGER("ett heltal"),
    STRING("en sträng"),

    ASSIGN("'='"),
    PLUS("'+'"),
    MINUS("'-'"),
    EQUAL_EQUAL("'=='"),
    LESS_EQUAL("'<='"),
    GREATER_EQUAL("'>='"),
    LESS_THAN("'<'"),
    GREATER_THAN("'>'"),
    NOT_EQUAL("'!='"),

    LEFT_PAREN("'('"),
    RIGHT_PAREN("')'"),
    LEFT_BRACE("'{'"),
    RIGHT_BRACE("'}'"),
    SEMICOLON("';'"),
    COMMA("','"),

    EOF("slut på inmatningen");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
