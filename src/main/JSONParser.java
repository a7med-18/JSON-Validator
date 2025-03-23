package main;

import java.util.List; // Import for using List

public class JSONParser {
    private final List<Token> tokens; // List of tokens to parse
    private int index = 0; // Current index in the token list

    // Constructor for the parser, initializing with the list of tokens
    public JSONParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Method to parse the tokens
    public void parse() throws Exception {
        parseValue(); // Start parsing values
        if (tokens.get(index).getType() != Token.Type.END) { // Ensure all tokens have been parsed
            throw new Exception("Unexpected token after JSON object at index " + index + ": " + tokens.get(index));
        }
    }

    // Method to parse a value
    private void parseValue() throws Exception {
        Token token = tokens.get(index); // Get the current token
        switch (token.getType()) {
            case LEFT_BRACE: // Handle JSON object
                parseObject();
                break;
            case LEFT_BRACKET: // Handle JSON array
                parseArray();
                break;
            case STRING: // Handle string
            case NUMBER: // Handle number
            case BOOLEAN: // Handle boolean
            case NULL: // Handle null
                index++; // Move to the next token
                break;
            default:
                // Throw an exception for unexpected tokens
                throw new Exception("Unexpected token at index " + index + ": " + token);
        }
    }

    // Method to parse a JSON object
    private void parseObject() throws Exception {
        index++; // Move past the opening brace
        if (tokens.get(index).getType() == Token.Type.RIGHT_BRACE) { // Handle empty object
            index++; // Move past the closing brace
            return;
        }
        while (true) {
            if (tokens.get(index).getType() != Token.Type.STRING) { // Ensure key is a string
                throw new Exception("Expected string key at index " + index + ": " + tokens.get(index));
            }
            index++; // Move past the key
            if (tokens.get(index).getType() != Token.Type.COLON) { // Ensure colon follows key
                throw new Exception("Expected colon after key at index " + index + ": " + tokens.get(index));
            }
            index++; // Move past the colon
            parseValue(); // Parse the value
            if (tokens.get(index).getType() == Token.Type.RIGHT_BRACE) { // Check for end of object
                index++; // Move past the closing brace
                return;
            }
            if (tokens.get(index).getType() != Token.Type.COMMA) { // Ensure comma follows value
                throw new Exception("Expected comma between key-value pairs at index " + index + ": " + tokens.get(index));
            }
            index++; // Move past the comma
        }
    }

    // Method to parse a JSON array
    private void parseArray() throws Exception {
        index++; // Move past the opening bracket
        if (tokens.get(index).getType() == Token.Type.RIGHT_BRACKET) { // Handle empty array
            index++; // Move past the closing bracket
            return;
        }
        while (true) {
            parseValue(); // Parse the value
            if (tokens.get(index).getType() == Token.Type.RIGHT_BRACKET) { // Check for end of array
                index++; // Move past the closing bracket
                return;
            }
            if (tokens.get(index).getType() != Token.Type.COMMA) { // Ensure comma follows value
                throw new Exception("Expected comma in array at index " + index + ": " + tokens.get(index));
            }
            index++; // Move past the comma
        }
    }
}