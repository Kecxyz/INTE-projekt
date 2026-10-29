package grupp8;

import grupp8.Interpreter.BlockNode;
import grupp8.Interpreter.Interpreter;
import grupp8.Lexer.Lexer;
import grupp8.Parser.Parser;
import grupp8.Parser.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramIntegrationTest {


    @Test
    void executesSingleDeclaration() {
        Environment environment = execute("let x = 1;");

        assertEquals(1, environment.getVariable("x"));
    }

    @Test
    void executesAssignmentAfterDeclaration() {
        Environment environment = execute("let x = 1; x = x + 2;");

        assertEquals(3, environment.getVariable("x"));
    }


    @Test
    void executesOnlyThenBranchWhenConditionIsTrue() {
        String source = """
                let result = 0;
                if (1 < 2) {
                    result = 10;
                } else {
                    result = unknown;
                }
                """;

        Environment environment = execute(source);

        assertEquals(10, environment.getVariable("result"));
    }

    @Test
    void executesOnlyElseBranchWhenConditionIsFalse() {
        String source = """
                let result = 0;
                if (1 > 2) {
                    result = unknown;
                } else {
                    result = 20;
                }
                """;

        Environment environment = execute(source);

        assertEquals(20, environment.getVariable("result"));
    }

    @Test
    void skipsFalseIfWithoutElse() {
        Environment environment = execute("let x = 1; if (1 > 2) { x = unknown; }");

        assertEquals(1, environment.getVariable("x"));
    }

    @Test
    void executesElseIfBranch() {
        String source = """
                let result = 0;
                if (1 > 2) {
                    result = 1;
                } else if (2 == 2) {
                    result = 2;
                } else {
                    result = 3;
                }
                """;

        Environment environment = execute(source);

        assertEquals(2, environment.getVariable("result"));
    }

    @Test
    void skipsWhileWithFalseCondition() {
        Environment environment = execute("let count = 0; while (count > 0) { count = unknown; }");

        assertEquals(0, environment.getVariable("count"));
    }

    @Test
    @Timeout(5)
    void executesIfInsideWhile() {
        String source = """
                let count = 0;
                let total = 0;
                while (count < 5) {
                    if (count < 3) {
                        total = total + 2;
                    } else {
                        total = total + 1;
                    }
                    count = count + 1;
                }
                """;

        Environment environment = execute(source);

        assertEquals(5, environment.getVariable("count"));
        assertEquals(8, environment.getVariable("total"));
    }

    @Test
    @Timeout(5)
    void executesNestedWhileLoops() {
        String source = """
                let outer = 0;
                let total = 0;
                while (outer < 3) {
                    let inner = 0;
                    while (inner < 4) {
                        total = total + 1;
                        inner = inner + 1;
                    }
                    outer = outer + 1;
                }
                """;

        Environment environment = execute(source);

        assertEquals(12, environment.getVariable("total"));
    }


    @Test
    void callsFunctionWithoutParameters() {
        String source = "function answer() { return 42; } let result = answer();";

        Environment environment = execute(source);

        assertEquals(42, environment.getVariable("result"));
    }

    @Test
    void callsFunctionWithTwoParameters() {
        String source = """
                function add(left, right) {
                    return left + right;
                }
                let result = add(3, 4);
                """;

        Environment environment = execute(source);

        assertEquals(7, environment.getVariable("result"));
    }

    @Test
    void evaluatesNestedFunctionCalls() {
        String source = """
                function twice(value) { return value + value; }
                function add(left, right) { return left + right; }
                let result = add(twice(2), twice(3));
                """;

        Environment environment = execute(source);

        assertEquals(10, environment.getVariable("result"));
    }

    @Test
    void usesFunctionResultAsCondition() {
        String source = """
                function isPositive(value) { return value > 0; }
                let result = 0;
                if (isPositive(3)) {
                    result = 1;
                }
                """;

        Environment environment = execute(source);

        assertEquals(1, environment.getVariable("result"));
    }

    @Test
    void callsFunctionAsStatementAndUpdatesGlobalVariable() {
        String source = """
                function increment() { count = count + 1; }
                let count = 0;
                increment();
                increment();
                """;

        Environment environment = execute(source);

        assertEquals(2, environment.getVariable("count"));
    }

    @Test
    void keepsParametersAndLocalVariablesInsideFunction() {
        String source = """
                let value = 100;
                function calculate(value) {
                    let temporary = value + 1;
                    return temporary;
                }
                let result = calculate(4);
                """;

        Environment environment = execute(source);

        assertEquals(100, environment.getVariable("value"));
        assertEquals(5, environment.getVariable("result"));
        RuntimeException error = assertThrows(RuntimeException.class, () -> environment.getVariable("temporary"));
        assertTrue(error.getMessage().contains("Variabeln 'temporary' är inte definierad"));
    }

    @Test
    void readsGlobalsInsteadOfAnotherFunctionsLocals() {
        String source = """
                let value = 10;
                function read() { return value; }
                function caller() {
                    let value = 20;
                    return read();
                }
                let result = caller();
                """;

        Environment environment = execute(source);

        assertEquals(10, environment.getVariable("result"));
    }

    @Test
    void createsFreshLocalsForEachCall() {
        String source = """
                function counter() {
                    let count = 0;
                    count = count + 1;
                    return count;
                }
                let first = counter();
                let second = counter();
                """;

        Environment environment = execute(source);

        assertEquals(1, environment.getVariable("first"));
        assertEquals(1, environment.getVariable("second"));
    }

    @Test
    @Timeout(5)
    void returnsFromInsideIfAndWhile() {
        String source = """
                function findThree(value) {
                    while (value > 0) {
                        if (value == 3) {
                            return value;
                        }
                        value = value - 1;
                    }
                    return 0;
                }
                let result = findThree(5);
                """;

        Environment environment = execute(source);

        assertEquals(3, environment.getVariable("result"));
    }

    @Test
    void returnsWithoutValueAndSkipsRemainingStatements() {
        String source = """
                function stop() {
                    return;
                    let unused = unknown;
                }
                stop();
                let result = 1;
                """;

        Environment environment = execute(source);

        assertEquals(1, environment.getVariable("result"));
    }

    @Test
    @Timeout(5)
    void executesRecursiveFunction() {
        String source = """
                function sumTo(value) {
                    if (value == 0) {
                        return 0;
                    }
                    return value + sumTo(value - 1);
                }
                let result = sumTo(10);
                """;

        Environment environment = execute(source);

        assertEquals(55, environment.getVariable("result"));
    }


    @Test
    void rejectsUndefinedFunction() {
        RuntimeException error = assertThrows(RuntimeException.class, () -> execute("missing();"));

        assertTrue(error.getMessage().contains("Funktionen 'missing' är inte definierad"));
    }

    @Test
    void rejectsWrongNumberOfArguments() {
        String source = "function add(a, b) { return a + b; } add(1);";

        RuntimeException error = assertThrows(RuntimeException.class, () -> execute(source));

        assertTrue(error.getMessage().contains("förväntar sig 2 argument, men fick 1"));
    }

    @Test
    void rejectsUndefinedVariableDuringExecution() {
        RuntimeException error = assertThrows(RuntimeException.class, () -> execute("let x = unknown;"));

        assertTrue(error.getMessage().contains("Variabeln 'unknown' är inte definierad"));
    }

    @Test
    void rejectsNonBooleanIfCondition() {
        RuntimeException error = assertThrows(RuntimeException.class, () -> execute("if (1) {}"));

        assertTrue(error.getMessage().contains("Villkoret måste ge sant eller falskt"));
    }

    @Test
    void rejectsNonBooleanWhileCondition() {
        RuntimeException error = assertThrows(RuntimeException.class, () -> execute("while (\"text\") {}"));

        assertTrue(error.getMessage().contains("Villkoret måste ge sant eller falskt"));
    }

    @Test
    void rejectsSubtractionFromString() {
        RuntimeException error = assertThrows(RuntimeException.class, () -> execute("let x = \"text\" - 1;"));

        assertTrue(error.getMessage().contains("Beräkningen kräver heltal"));
    }

    @Test
    void rejectsReturnOutsideFunction() {
        ParserException error = assertThrows(ParserException.class, () -> execute("return 1;"));

        assertTrue(error.getMessage().contains("inuti en funktion"));
        assertTrue(error.getMessage().contains("1:1"));
    }

    @Test
    void rejectsDuplicateParameterNames() {
        String source = "function add(value, value) { return value; }";

        ParserException error = assertThrows(ParserException.class, () -> execute(source));

        assertTrue(error.getMessage().contains("Parametern 'value' har redan angetts"));
    }

    @Test
    void rejectsMissingArgumentAfterComma() {
        String source = "function add(a, b) { return a + b; } add(1,);";

        ParserException error = assertThrows(ParserException.class, () -> execute(source));

        assertTrue(error.getMessage().contains("fick ')'"));
    }

    @Test
    void rejectsMissingSemicolonAfterReturn() {
        ParserException error = assertThrows(ParserException.class,
                () -> execute("function answer() { return 42 }"));

        assertTrue(error.getMessage().contains("';'"));
    }

    @Test
    void rejectsFunctionDeclaredInsideBlock() {
        ParserException error = assertThrows(ParserException.class,
                () -> execute("{ function answer() { return 42; } }"));

        assertTrue(error.getMessage().contains("översta nivå"));
    }


    @Test
    @Timeout(5)
    void executesFiveThousandAssignments() {
        String source = "let count = 0;\n" + "count = count + 1;\n".repeat(5_000);

        Environment environment = execute(source);

        assertEquals(5_000, environment.getVariable("count"));
    }

    @Test
    @Timeout(5)
    void executesTenThousandLoopIterations() {
        String source = "let count = 0; while (count < 10000) { count = count + 1; }";

        Environment environment = execute(source);

        assertEquals(10_000, environment.getVariable("count"));
    }

    @Test
    @Timeout(5)
    void executesFunctionWithFiveThousandAssignments() {
        String source = "function count() { let value = 0;\n"
                + "value = value + 1;\n".repeat(5_000)
                + "return value; } let result = count();";

        Environment environment = execute(source);

        assertEquals(5_000, environment.getVariable("result"));
    }

    private Environment execute(String source) {
        Parser parser = new Parser(new Lexer(source).tokenize());
        BlockNode program = parser.parseProgram();
        Environment environment = new Environment();
        Interpreter interpreter = new Interpreter(environment);
        interpreter.interpret(program);
        return environment;
    }
}
