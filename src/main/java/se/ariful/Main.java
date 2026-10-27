package se.ariful;

import java.util.List;

// Runs the full pipeline: Lexer -> Parser -> Interpreter
public class Main {
    public static void main(String[] args) {
        // Sample source code matching your parser capabilities
        String sourceCode = "10 + 25 - 5";

        // 1. Lexer turns source text into tokens
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

        // 2. Parser turns tokens directly into your ASTNode objects
        Parser parser = new Parser(tokens);
        ParserExpression expression = parser.parseExpression();
        ASTNode programAST = ParserASTConverter.toAST(expression);

        // 3. Environment and Interpreter execute the AST via Visitor pattern
        Environment runtimeEnvironment = new Environment();
        Interpreter astInterpreter = new Interpreter(runtimeEnvironment);

        Object evaluationResult = programAST.accept(astInterpreter);

        System.out.println("Source Code: " + sourceCode);
        System.out.println("Evaluation Result: " + evaluationResult);
    }
}