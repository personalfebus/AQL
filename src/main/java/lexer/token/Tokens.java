package lexer.token;

public class Tokens {
    public static String eofType = EofToken.class.getName();
    public static String floatingNumberType = FloatingNumberToken.class.getName();
    public static String identifierType = IdentifierToken.class.getName();
    public static String integerNumberType = IntegerNumberToken.class.getName();
    public static String keywordType = KeywordToken.class.getName();
    public static String operatorType = OperatorToken.class.getName();
    public static String stringType = StringToken.class.getName();
    public static String symbolType = SymbolToken.class.getName();
}
