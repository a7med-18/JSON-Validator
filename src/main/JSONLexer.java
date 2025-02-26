package main;

import java.util.ArrayList;
import java.util.List;

public class JSONLexer {
    private final String input;
    private int pos = 0;
    private int depth = 0;
    private static final int MAX_DEPTH = 19; // Set a reasonable depth limit

    public JSONLexer(String input) throws Exception {
        this.input = input.trim();
        validateJsonStart(); // Ensure JSON starts correctly
    }

    public List<Token> tokenize() throws Exception {
        List<Token> tokens = new ArrayList<>();
        boolean expectColon = false;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '{' || c == '[') {
                depth++;
                if (depth > MAX_DEPTH) {
                    throw new Exception("Invalid JSON: Too deeply nested at position " + pos);
                }
                tokens.add(new Token(c == '{' ? Token.Type.LEFT_BRACE : Token.Type.LEFT_BRACKET, String.valueOf(c)));
                pos++;
                expectColon = (c == '{');
            } else if (c == '}' || c == ']') {
                depth--;
                tokens.add(new Token(c == '}' ? Token.Type.RIGHT_BRACE : Token.Type.RIGHT_BRACKET, String.valueOf(c)));
                pos++;
                expectColon = false;
            } else if (c == ':') {
                tokens.add(new Token(Token.Type.COLON, ":"));
                pos++;
                expectColon = false;
            } else if (c == ',') {
                tokens.add(new Token(Token.Type.COMMA, ","));
                pos++;
                expectColon = true;
            } else if (c == '"') {
                tokens.add(new Token(Token.Type.STRING, extractString()));
                expectColon = true;
            } else if (Character.isDigit(c) || c == '-') {
                if (expectColon) {
                    throw new Exception("Expected colon before value at position " + pos);
                }
                tokens.add(new Token(Token.Type.NUMBER, extractNumber()));
            } else if (input.startsWith("true", pos)) {
                tokens.add(new Token(Token.Type.BOOLEAN, "true"));
                pos += 4;
                expectColon = false;
            } else if (input.startsWith("false", pos)) {
                tokens.add(new Token(Token.Type.BOOLEAN, "false"));
                pos += 5;
                expectColon = false;
            } else if (input.startsWith("null", pos)) {
                tokens.add(new Token(Token.Type.NULL, "null"));
                pos += 4;
                expectColon = false;
            } else {
                throw new Exception("Unexpected character at position " + pos + ": " + c);
            }
        }
        tokens.add(new Token(Token.Type.END, ""));
        return tokens;
    }

    private void validateJsonStart() throws Exception {
        if (!(input.startsWith("{") || input.startsWith("["))) {
            throw new Exception("Invalid JSON: A JSON payload should be an object or array, not a string.");
        }
    }

    private String extractString() throws Exception {
        int start = ++pos;
        while (pos < input.length() && input.charAt(pos) != '"') {
            if (input.charAt(pos) == '\\') {
                throw new Exception("Illegal backslash escape at position " + pos);
            }
            if (input.charAt(pos) == '\t') {
                throw new Exception("Illegal tab character in string at position " + pos);
            }
            if (input.charAt(pos) == '\n' || input.charAt(pos) == '\r') {
                throw new Exception("Illegal line break in string at position " + pos);
            }
            pos++;
        }
        if (pos >= input.length()) throw new Exception("Unterminated string starting at position " + start);
        String str = input.substring(start, pos);
        pos++;
        return str;
    }


    private String extractNumber() throws Exception {
        int start = pos;
        boolean hasDecimal = false;
        boolean hasExponent = false;

        if (input.charAt(pos) == '0' && pos + 1 < input.length() && Character.isDigit(input.charAt(pos + 1))) {
            throw new Exception("Invalid number format: Numbers cannot have leading zeros at position " + pos);
        }
        if (input.charAt(pos) == '0' && pos + 1 < input.length() && (input.charAt(pos + 1) == 'x' || input.charAt(pos + 1) == 'X')) {
            throw new Exception("Invalid number format: Hexadecimal numbers are not allowed at position " + pos);
        }

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.') {
                if (hasDecimal) {
                    throw new Exception("Invalid number format: Multiple decimal points at position " + pos);
                }
                hasDecimal = true;
                pos++;
            } else if (c == 'e' || c == 'E') {
                if (hasExponent) {
                    throw new Exception("Invalid number format: Multiple exponents at position " + pos);
                }
                hasExponent = true;
                pos++;

                // Ensure 'e' or 'E' is followed by a valid digit or a sign with a digit
                if (pos >= input.length() ||
                        (!Character.isDigit(input.charAt(pos)) &&
                                input.charAt(pos) != '+' && input.charAt(pos) != '-')) {
                    throw new Exception("Invalid number format: Exponent must be followed by digits at position " + pos);
                }

                // Skip '+' or '-' in the exponent if present
                if (input.charAt(pos) == '+' || input.charAt(pos) == '-') {
                    pos++;
                    if (pos >= input.length() || !Character.isDigit(input.charAt(pos))) {
                        throw new Exception("Invalid number format: Exponent sign must be followed by digits at position " + pos);
                    }
                }
            } else {
                break;
            }
        }

        if (input.charAt(pos - 1) == '.' || input.charAt(pos - 1) == 'e' || input.charAt(pos - 1) == 'E') {
            throw new Exception("Invalid number format: Number cannot end with an exponent or decimal at position " + (pos - 1));
        }

        return input.substring(start, pos);
    }
}
