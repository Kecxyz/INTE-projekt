package se.ariful;

public interface ParserExpression {
    
    record IntegerLiteral(int value) implements ParserExpression {
        
    }

    record BinaryExpression(ParserExpression left, TokenType operator, ParserExpression right) implements ParserExpression {

    }

    record StringLiteral(String value) implements ParserExpression {
    }

}
