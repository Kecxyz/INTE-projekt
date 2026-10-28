package grupp8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import grupp8.Interpreter.BlockNode;
import grupp8.Interpreter.Interpreter;
import grupp8.Lexer.Lexer;
import grupp8.Parser.Parser;

// Runs the full pipeline: Lexer -> Parser -> Interpreter
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Användning: grupp8.Main [programfil]");
        }

        String sourceCode;
        if (args.length == 1) {
            sourceCode = Files.readString(Path.of(args[0]));
        } else {
            // Sample source code matching your parser capabilities
            sourceCode = """
                    function sumTo(limit) {
                        let total = 0;
                        while (limit > 0) {
                            total = total + limit;
                            limit = limit - 1;
                        }
                        return total;
                    }

                    let result = sumTo(100);
                    if (result == 5050) {
                        print(result);
                    } else {
                        print("Oväntat resultat");
                    }
                    """;
        }

        // 1. Lexer turns source text into tokens
        Lexer lexer = new Lexer(sourceCode);
        // 2. Parser turns tokens directly into your ASTNode objects
        Parser parser = new Parser(lexer.tokenize());
        BlockNode program = parser.parseProgram();
        // 3. Environment and Interpreter execute the AST via Visitor pattern
        Interpreter interpreter = new Interpreter(new Environment());
        interpreter.interpret(program);
    }
}
