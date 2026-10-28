package grupp8.Parser;

import grupp8.Interpreter.ASTNode;
import grupp8.Interpreter.AssignNode;
import grupp8.Interpreter.BlockNode;
import grupp8.Interpreter.FunctionNode;
import grupp8.Interpreter.IfNode;
import grupp8.Interpreter.LetNode;
import grupp8.Interpreter.PrintNode;
import grupp8.Interpreter.ReturnNode;
import grupp8.Interpreter.WhileNode;
import grupp8.Lexer.Token;
import grupp8.Lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private boolean insideFunction = false;

    public Parser(List<Token> tokens) {
        this.tokens = List.copyOf(tokens);
        if (this.tokens.isEmpty() || this.tokens.get(this.tokens.size() - 1).type() != TokenType.EOF) {
            throw new ParserException("Tokenlistan måste avslutas med en markör för slut på inmatningen");
        }
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        Token token = peek();
        if (token.type() != TokenType.EOF) {
            current++;
        }
        return token;
    }

    private boolean match(TokenType type) {
        if (peek().type() != type) {
            return false;
        }
        advance();
        return true;
    }

    private Token consume(TokenType expected, String message) {
        if (peek().type() == expected) {
            return advance();
        }
        throw error(peek(), message);
    }

    private ParserException error(Token token, String message) {
        return new ParserException(
                message + " vid " + token.line() + ":" + token.column()
                        + ", fick " + token.type().getDescription()
        );
    }

    public BlockNode parseProgram() {
        List<ASTNode> statements = new ArrayList<>();
        while (peek().type() != TokenType.EOF) {
            if (peek().type() == TokenType.FUNCTION) {
                statements.add(parseFunctionStatement());
            } else {
                statements.add(parseStatement());
            }
        }
        consume(TokenType.EOF, "Förväntade slut på programmet");
        return new BlockNode(statements);
    }

    public ASTNode parseStatement() {
        return switch (peek().type()) {
            case LET -> parseLetStatement();
            case IDENTIFIER -> parseIdentifierStatement();
            case PRINT -> parsePrintStatement();
            case LEFT_BRACE -> parseBlock();
            case IF -> parseIfStatement();
            case WHILE -> parseWhileStatement();
            case RETURN -> parseReturnStatement();
            case FUNCTION -> throw error(peek(), "Funktioner måste deklareras på programmets översta nivå");
            default -> throw error(peek(), "Förväntade en instruktion");
        };
    }

    private LetNode parseLetStatement() {
        consume(TokenType.LET, "Förväntade 'let'");
        Token name = consume(TokenType.IDENTIFIER, "Förväntade ett variabelnamn efter 'let'");
        consume(TokenType.ASSIGN, "Förväntade '=' efter variabelnamnet");
        ASTNode expression = ParserASTConverter.toAST(parseExpression());
        consume(TokenType.SEMICOLON, "Förväntade ';' efter deklarationen");
        return new LetNode(name.value(), expression);
    }

    private ASTNode parseIdentifierStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Förväntade ett variabelnamn");
        if (peek().type() == TokenType.LEFT_PAREN) {
            ParserExpression.CallExpression call = parseCall(name.value());
            consume(TokenType.SEMICOLON, "Förväntade ';' efter funktionsanropet");
            return ParserASTConverter.toAST(call);
        }

        consume(TokenType.ASSIGN, "Förväntade '=' efter variabelnamnet");
        ASTNode expression = ParserASTConverter.toAST(parseExpression());
        consume(TokenType.SEMICOLON, "Förväntade ';' efter tilldelningen");
        return new AssignNode(name.value(), expression);
    }

    private PrintNode parsePrintStatement() {
        consume(TokenType.PRINT, "Förväntade 'print'");
        consume(TokenType.LEFT_PAREN, "Förväntade '(' efter 'print'");
        ASTNode expression = ParserASTConverter.toAST(parseExpression());
        consume(TokenType.RIGHT_PAREN, "Förväntade ')' efter utskriftsuttrycket");
        consume(TokenType.SEMICOLON, "Förväntade ';' efter utskriften");
        return new PrintNode(expression);
    }

    private IfNode parseIfStatement() {
        consume(TokenType.IF, "Förväntade 'if'");
        consume(TokenType.LEFT_PAREN, "Förväntade '(' efter 'if'");
        ASTNode condition = ParserASTConverter.toAST(parseExpression());
        consume(TokenType.RIGHT_PAREN, "Förväntade ')' efter villkoret");
        BlockNode thenBranch = parseBlock();

        ASTNode elseBranch = null;
        if (match(TokenType.ELSE)) {
            if (peek().type() == TokenType.IF) {
                elseBranch = parseIfStatement();
            } else {
                elseBranch = parseBlock();
            }
        }
        return new IfNode(condition, thenBranch, elseBranch);
    }

    private WhileNode parseWhileStatement() {
        consume(TokenType.WHILE, "Förväntade 'while'");
        consume(TokenType.LEFT_PAREN, "Förväntade '(' efter 'while'");
        ASTNode condition = ParserASTConverter.toAST(parseExpression());
        consume(TokenType.RIGHT_PAREN, "Förväntade ')' efter villkoret");
        BlockNode body = parseBlock();
        return new WhileNode(condition, body);
    }

    private BlockNode parseBlock() {
        consume(TokenType.LEFT_BRACE, "Förväntade '{'");
        List<ASTNode> statements = new ArrayList<>();
        while (peek().type() != TokenType.RIGHT_BRACE && peek().type() != TokenType.EOF) {
            statements.add(parseStatement());
        }
        consume(TokenType.RIGHT_BRACE, "Förväntade '}' efter kodblocket");
        return new BlockNode(statements);
    }

    private FunctionNode parseFunctionStatement() {
        consume(TokenType.FUNCTION, "Förväntade 'function'");
        Token name = consume(TokenType.IDENTIFIER, "Förväntade ett funktionsnamn");
        consume(TokenType.LEFT_PAREN, "Förväntade '(' efter funktionsnamnet");

        List<String> parameters = new ArrayList<>();
        if (peek().type() != TokenType.RIGHT_PAREN) {
            do {
                Token parameter = consume(TokenType.IDENTIFIER, "Förväntade ett parameternamn");
                if (parameters.contains(parameter.value())) {
                    throw error(parameter, "Parametern '" + parameter.value() + "' har redan angetts");
                }
                parameters.add(parameter.value());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Förväntade ')' efter parametrarna");

        insideFunction = true;
        BlockNode body;
        try {
            body = parseBlock();
        } finally {
            insideFunction = false;
        }
        return new FunctionNode(name.value(), parameters, body);
    }

    private ReturnNode parseReturnStatement() {
        if (!insideFunction) {
            throw error(peek(), "Nyckelordet 'return' får bara användas inuti en funktion");
        }
        consume(TokenType.RETURN, "Förväntade 'return'");
        ASTNode expression = null;
        if (peek().type() != TokenType.SEMICOLON) {
            expression = ParserASTConverter.toAST(parseExpression());
        }
        consume(TokenType.SEMICOLON, "Förväntade ';' efter returvärdet");
        return new ReturnNode(expression);
    }

    public ParserExpression parseExpression() {
        return parseEquality();
    }

    public ParserExpression parseCompleteExpression() {
        ParserExpression expression = parseExpression();
        consume(TokenType.EOF, "Förväntade slut på inmatningen");
        return expression;
    }

    private ParserExpression parseEquality() {
        ParserExpression expression = parseComparison();
        while (peek().type() == TokenType.EQUAL_EQUAL || peek().type() == TokenType.NOT_EQUAL) {
            Token operator = advance();
            ParserExpression right = parseComparison();
            expression = new ParserExpression.BinaryExpression(expression, operator.type(), right);
        }
        return expression;
    }

    private ParserExpression parseComparison() {
        ParserExpression expression = parseAddition();
        while (peek().type() == TokenType.LESS_THAN
                || peek().type() == TokenType.GREATER_THAN
                || peek().type() == TokenType.LESS_EQUAL
                || peek().type() == TokenType.GREATER_EQUAL) {
            Token operator = advance();
            ParserExpression right = parseAddition();
            expression = new ParserExpression.BinaryExpression(expression, operator.type(), right);
        }
        return expression;
    }

    private ParserExpression parseAddition() {
        ParserExpression expression = parseUnary();
        while (peek().type() == TokenType.PLUS || peek().type() == TokenType.MINUS) {
            Token operator = advance();
            ParserExpression right = parseUnary();
            expression = new ParserExpression.BinaryExpression(expression, operator.type(), right);
        }
        return expression;
    }

    private ParserExpression parseUnary() {
        if (match(TokenType.MINUS)) {
            return new ParserExpression.UnaryExpression(TokenType.MINUS, parseUnary());
        }
        return parsePrimary();
    }

    private ParserExpression parsePrimary() {
        Token token = peek();
        if (token.type() == TokenType.INTEGER) {
            try {
                int value = Integer.parseInt(token.value());
                advance();
                return new ParserExpression.IntegerLiteral(value);
            } catch (NumberFormatException exception) {
                throw error(token, "Ogiltigt heltal '" + token.value() + "'");
            }
        }

        if (match(TokenType.STRING)) {
            return new ParserExpression.StringLiteral(token.value());
        }

        if (match(TokenType.IDENTIFIER)) {
            if (peek().type() == TokenType.LEFT_PAREN) {
                return parseCall(token.value());
            }
            return new ParserExpression.VariableExpression(token.value());
        }

        if (match(TokenType.LEFT_PAREN)) {
            ParserExpression expression = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Förväntade ')'");
            return expression;
        }
        throw error(token, "Förväntade ett uttryck");
    }

    private ParserExpression.CallExpression parseCall(String name) {
        consume(TokenType.LEFT_PAREN, "Förväntade '(' efter funktionsnamnet");
        List<ParserExpression> arguments = new ArrayList<>();
        if (peek().type() != TokenType.RIGHT_PAREN) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Förväntade ')' efter argumenten");
        return new ParserExpression.CallExpression(name, arguments);
    }
}
