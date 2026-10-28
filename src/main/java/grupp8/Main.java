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

        Lexer lexer = new Lexer(sourceCode);
        Parser parser = new Parser(lexer.tokenize());
        BlockNode program = parser.parseProgram();
        Interpreter interpreter = new Interpreter(new Environment());
        interpreter.interpret(program);
    }
}
