package grupp8;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import grupp8.Interpreter.ASTNode;
import grupp8.Interpreter.AssignNode;
import grupp8.Interpreter.BlockNode;
import grupp8.Interpreter.IfNode;
import grupp8.Interpreter.Interpreter;
import grupp8.Interpreter.LetNode;
import grupp8.Interpreter.PrintNode;
import grupp8.Lexer.Lexer;
import grupp8.Lexer.TokenType;
import grupp8.Parser.Parser;
import grupp8.Parser.ParserASTConverter;
import grupp8.Parser.ParserException;
import grupp8.Parser.ParserExpression;

class ParserIntegrationTest {


    @Test
    void parsesSingleIntegerFromSource() {
        Parser parser = new Parser(new Lexer("7").tokenize());

        assertEquals(new ParserExpression.IntegerLiteral(7), parser.parseCompleteExpression());
    }

    @Test
    void parsesStringFromSource() {
        Parser parser = new Parser(new Lexer("\"Hello\"").tokenize());

        assertEquals(new ParserExpression.StringLiteral("Hello"), parser.parseCompleteExpression());
    }

    @Test
    void rejectsEmptyExpression() {
        assertUnexpectedEnd("");
    }


    @Test
    void parsesCompleteExpression() {
        Lexer lexer = new Lexer("3 + 2");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        TokenType.PLUS,
                        new ParserExpression.IntegerLiteral(2)
                );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void parsesSubtractionOfNegativeNumber() {
        Lexer lexer = new Lexer("10 - -3");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression negativeThree = new ParserExpression.UnaryExpression(TokenType.MINUS, new ParserExpression.IntegerLiteral(3));

        ParserExpression expected = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(10),
            TokenType.MINUS,
            negativeThree
        );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void parsesNegatedParenthesizedExpression() {
        Lexer lexer = new Lexer("-(3 + 2)");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression addition = new ParserExpression.BinaryExpression(
            new ParserExpression.IntegerLiteral(3),
            TokenType.PLUS,
            new ParserExpression.IntegerLiteral(2)
        );

        ParserExpression expected = new ParserExpression.UnaryExpression(
            TokenType.MINUS,
            addition
        );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void parsesComparisonOperators() {
        assertComparison("3 < 5", TokenType.LESS_THAN);
        assertComparison("3 > 5", TokenType.GREATER_THAN);
        assertComparison("3 <= 5", TokenType.LESS_EQUAL);
        assertComparison("3 >= 5", TokenType.GREATER_EQUAL);
    }

    @Test
    void parsesEqualityOperators() {
        assertComparison("3 == 5", TokenType.EQUAL_EQUAL);
        assertComparison("3 != 5", TokenType.NOT_EQUAL);
    }

    @Test
    void groupsArithmeticBeforeComparison() {
        Lexer lexer = new Lexer("price + 20 >= total - 10");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression left =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.VariableExpression("price"),
                        TokenType.PLUS,
                        new ParserExpression.IntegerLiteral(20)
                );

        ParserExpression right =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.VariableExpression("total"),
                        TokenType.MINUS,
                        new ParserExpression.IntegerLiteral(10)
                );

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        left,
                        TokenType.GREATER_EQUAL,
                        right
                );

        assertEquals(expected, parser.parseCompleteExpression());
    }

    @Test
    void groupsComparisonBeforeEquality() {
        Lexer lexer = new Lexer("3 < 5 == 2 < 4");
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression left =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        TokenType.LESS_THAN,
                        new ParserExpression.IntegerLiteral(5)
                );

        ParserExpression right =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(2),
                        TokenType.LESS_THAN,
                        new ParserExpression.IntegerLiteral(4)
                );

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        left,
                        TokenType.EQUAL_EQUAL,
                        right
                );

        assertEquals(expected, parser.parseCompleteExpression());
    }


    @Test
    void rejectsMissingEqualityOperand() {
        assertUnexpectedEnd("3 ==");
    }

    @Test
    void rejectsMissingClosingParenthesis() {
        assertUnexpectedEnd("(3 + 2");
    }

    @Test
    void rejectsEmptyParentheses() {
        Parser parser = new Parser(new Lexer("()").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("fick ')'"));
        assertTrue(error.getMessage().contains("1:2"));
    }

    @Test
    void rejectsConsecutivePlusOperators() {
        Parser parser = new Parser(new Lexer("1 + + 2").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseCompleteExpression());

        assertTrue(error.getMessage().contains("fick '+'"));
        assertTrue(error.getMessage().contains("1:5"));
    }

    @Test
    void rejectsExtraTokenAfterExpression() {
        Lexer lexer = new Lexer("3 4");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseCompleteExpression()
        );

        assertTrue(error.getMessage().contains("ett heltal"));
        assertTrue(error.getMessage().contains("1:3"));
    }


    @Test
    void acceptsSimpleLetStatement() {
        Parser parser = new Parser(new Lexer("let x = 1;").tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsSimplePrintStatement() {
        Parser parser = new Parser(new Lexer("print(1);").tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsLetStatement() {
        Lexer lexer = new Lexer("let price = 100 + 20;");
        Parser parser = new Parser(lexer.tokenize());

        assertDoesNotThrow(() -> parser.parseStatement());
    }

    @Test
    void acceptsAssignmentStatement() {
        Lexer lexer = new Lexer("price = price + 20;");
        Parser parser = new Parser(lexer.tokenize());

        assertDoesNotThrow(() -> parser.parseStatement());
    }

    @Test
    void acceptsPrintStatement() {
        Lexer lexer = new Lexer("print(price + 20);");
        Parser parser = new Parser(lexer.tokenize());

        assertDoesNotThrow(() -> parser.parseStatement());
    }

    @Test
    void rejectsLetWithoutSemicolon() {
        Lexer lexer = new Lexer("let price = 100 + 20");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseStatement()
        );

        assertTrue(error.getMessage().contains("';'"));
    }

    @Test
    void rejectsLetWithoutValue() {
        Parser parser = new Parser(new Lexer("let x = ;").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick ';'"));
        assertTrue(error.getMessage().contains("1:9"));
    }

    @Test
    void rejectsKeywordAsVariableName() {
        Parser parser = new Parser(new Lexer("let if = 1;").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick 'if'"));
        assertTrue(error.getMessage().contains("1:5"));
    }

    @Test
    void rejectsAssignmentWithoutEquals() {
        Lexer lexer = new Lexer("price 100;");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseStatement()
        );

        assertTrue(error.getMessage().contains("'='"));
    }

    @Test
    void rejectsPrintWithoutClosingParenthesis() {
        Lexer lexer = new Lexer("print(price + 20;");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseStatement()
        );

        assertTrue(error.getMessage().contains("')'"));
    }

    @Test
    void rejectsPrintWithoutExpression() {
        Parser parser = new Parser(new Lexer("print();").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick ')'"));
        assertTrue(error.getMessage().contains("1:7"));
    }

    @Test
    void reportsErrorOnCorrectLineAfterValidStatement() {
        String source = "let x = 1;\nprint();";
        Parser parser = new Parser(new Lexer(source).tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick ')'"));
        assertTrue(error.getMessage().contains("2:7"));
    }


    @Test
    void acceptsEmptyProgram() {
        Parser parser = new Parser(new Lexer("").tokenize());

        BlockNode program = parser.parseProgram();

        assertEquals(0, program.getStatementList().size());
    }

    @Test
    void acceptsMultipleStatements() {
        String source = """
                let price = 100;
                price = price + 20;
                print(price);
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        BlockNode program = parser.parseProgram();

        assertEquals(3, program.getStatementList().size());
        assertInstanceOf(LetNode.class, program.getStatementList().get(0));
        assertInstanceOf(AssignNode.class, program.getStatementList().get(1));
        assertInstanceOf(PrintNode.class, program.getStatementList().get(2));
    }

    @Test
    void rejectsUnexpectedTokenAfterStatement() {
        Lexer lexer = new Lexer("let price = 100; 42");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseProgram()
        );

        assertTrue(error.getMessage().contains("ett heltal"));
    }

    @Test
    void acceptsEmptyBlock() {
        Parser parser = new Parser(new Lexer("{}").tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsNestedBlocks() {
        String source = """
                {
                    let price = 100;
                    {
                        price = price + 20;
                        print(price);
                    }
                }
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void rejectsBlockWithoutClosingBrace() {
        Lexer lexer = new Lexer("{ let price = 100;");
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseProgram()
        );

        assertTrue(error.getMessage().contains("'}'"));
    }

    @Test
    void acceptsIfWithoutElse() {
        String source = """
                if (price >= 100) {
                    print(price);
                }
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsIfElse() {
        String source = """
                if (price >= 100) {
                    print("Dyrt");
                } else {
                    print("Billigt");
                }
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        BlockNode program = parser.parseProgram();

        assertEquals(1, program.getStatementList().size());
        IfNode conditional = assertInstanceOf(IfNode.class, program.getStatementList().get(0));
        assertInstanceOf(BlockNode.class, conditional.getThenBranch());
        assertNotNull(conditional.getElseBranch());
        assertInstanceOf(BlockNode.class, conditional.getElseBranch());
    }

    @Test
    void rejectsIfWithoutCondition() {
        Parser parser = new Parser(new Lexer("if () {}").tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseProgram()
        );

        assertTrue(error.getMessage().contains("fick ')'"));
    }

    @Test
    void rejectsIfWithoutBlock() {
        Parser parser = new Parser(new Lexer("if (1 < 2) print(1);").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("'{'"));
        assertTrue(error.getMessage().contains("fick 'print'"));
        assertTrue(error.getMessage().contains("1:12"));
    }

    @Test
    void rejectsElseWithoutIf() {
        Parser parser = new Parser(new Lexer("else {}").tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick 'else'"));
        assertTrue(error.getMessage().contains("1:1"));
    }

    @Test
    void acceptsNestedIfElseStatements() {
        String source = """
                if (price > 0) {
                    if (price < 100) {
                        print("Small");
                    } else {
                        print("Large");
                    }
                } else {
                    print("Zero or negative");
                }
                """;
        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsWhileStatement() {
        String source = """
                let count = 3;
                while (count > 0) {
                    print(count);
                    count = count - 1;
                }
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void acceptsWhileInsideIf() {
        String source = """
                if (price > 0) {
                    while (price > 0) {
                        price = price - 1;
                    }
                }
                """;

        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    void rejectsWhileWithoutBlock() {
        String source = "while (price > 0) print(price);";

        Parser parser = new Parser(new Lexer(source).tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseProgram()
        );

        assertTrue(error.getMessage().contains("'{'"));
    }


    @Test
    @Timeout(5)
    void acceptsFiveThousandStatements() {
        String source = "let count = 0;\n" + "count = count + 1;\n".repeat(4_999);
        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }

    @Test
    @Timeout(5)
    void rejectsInvalidStatementAfterFiveThousandValidStatements() {
        String source = "let count = 0;\n" + "count = count + 1;\n".repeat(4_999) + "print();";
        Parser parser = new Parser(new Lexer(source).tokenize());

        ParserException error = assertThrows(ParserException.class, () -> parser.parseProgram());

        assertTrue(error.getMessage().contains("fick ')'"));
        assertTrue(error.getMessage().contains("5001:7"));
    }

    @Test
    @Timeout(5)
    void acceptsFiftyNestedBlocks() {
        String source = "{".repeat(50) + "let x = 1;" + "}".repeat(50);
        Parser parser = new Parser(new Lexer(source).tokenize());

        assertDoesNotThrow(() -> parser.parseProgram());
    }


    @Test
    void evaluatesSubtractionOfNegativeNumber() {
        Parser parser = new Parser(new Lexer("10 - -3").tokenize());
        ASTNode ast = ParserASTConverter.toAST(parser.parseCompleteExpression());
        Interpreter interpreter = new Interpreter(new Environment());

        assertEquals(13, interpreter.interpret(ast));
    }

    @Test
    void evaluatesNegatedParenthesizedExpression() {
        Parser parser = new Parser(new Lexer("-(3 + 2)").tokenize());
        ASTNode ast = ParserASTConverter.toAST(parser.parseCompleteExpression());
        Interpreter interpreter = new Interpreter(new Environment());

        assertEquals(-5, interpreter.interpret(ast));
    }

    private void assertComparison(String source, TokenType operator) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.tokenize());

        ParserExpression expected =
                new ParserExpression.BinaryExpression(
                        new ParserExpression.IntegerLiteral(3),
                        operator,
                        new ParserExpression.IntegerLiteral(5)
                );

        assertEquals(expected, parser.parseCompleteExpression(), source);
    }

    private void assertUnexpectedEnd(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.tokenize());

        ParserException error = assertThrows(
                ParserException.class,
                () -> parser.parseCompleteExpression()
        );

        assertTrue(error.getMessage().contains("slut på inmatningen"));
    }
}
