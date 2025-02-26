package main;

public class Token {
    // Enum for different token types
    public enum Type {
        LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET, COLON, COMMA, STRING, NUMBER, BOOLEAN, NULL, END
    }

    private Type type; // The type of the token
    private String value; // The value of the token

    // Constructor for creating a token
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    // Getter for the token type
    public Type getType() {
        return type;
    }

    // Getter for the token value
    public String getValue() {
        return value;
    }

    // Override the toString method for a readable representation of the token
    @Override
    public String toString() {
        return type + "(" + value + ")";
    }
}