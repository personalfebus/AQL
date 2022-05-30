package lexer.token;

import lexer.token.exception.BadNumberSyntaxError;

/**
 * Число с плавающей точкой
 */
public class FloatingNumberToken implements Token {
    private final double number;
    private final int line;
    private final int position;

    public FloatingNumberToken(String input, int line, int position) {
        this.number = parseNumber(input);
        this.line = line;
        this.position = position;
    }

    private double parseNumber(String input) {
        return Double.parseDouble(input);
    }

    private long construct10(String input) throws BadNumberSyntaxError {
        //[0-9]+
        long a = 0;
        for (int i = 0; i < input.length(); i++) {
            int digit = lex10Symbol(input.charAt(i));
            a = a*10 + digit;
        }
        return a;
    }

    private int lex10Symbol(int code) throws BadNumberSyntaxError {
        if (code > 47 && code < 58) {
            return code - 48;
        } else throw new BadNumberSyntaxError(line, position);
    }

    @Override
    public String getType() {
        return FloatingNumberToken.class.getName();
    }

    @Override
    public String getBody() {
        return String.valueOf(number);
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public double getNumber() {
        return number;
    }
}
