package parser;

import lexer.ILexer;
import lexer.Lexer;
import lexer.token.EofToken;
import lexer.token.Token;
import lexer.token.Tokens;
import parser.ast.AstType;
import parser.ast.constraint.AstConstraint;
import parser.ast.function.AstFunction;
import parser.ast.AstProgram;
import parser.ast.function.AstColumnDefinition;
import parser.ast.function.table.AstCreateTableFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;
import parser.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    private void nextToken() {
        if (lexer.hasTokens()) {
            currentToken = lexer.nextToken();
        } else {
            currentToken = new EofToken();
        }
    }

    @Override
    public AstProgram parse(String input) throws SyntaxException {
        return parseProgram();
    }

    private AstProgram parseProgram() throws SyntaxException {
        nextToken();
        AstProgram program = new AstProgram();

        for (;;) {
            AstFunction function = parseFunction();
            program.addFunction(function);

            if (currentToken.getType().equals(Tokens.eofType)) {
                break;
            }
        }

        return program;
    }

    //todo interface subclasses + parse
    private AstFunction parseFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);

        if (currentToken.getBody().equalsIgnoreCase("create")) {
            return parseCreateTableFunction();
        }

        return null;
    }

    //todo
    private AstCreateTableFunction parseCreateTableFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("create");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("table");
        nextToken();
        boolean hasIfNotExistPrefix = parseIfNotExistPrefix();
        AstTableName tableName = parseTableName();
        List<AstColumnDefinition> columnDefinitionList = parseCreateTableFunctionBody();

        return null;
    }

    private List<AstColumnDefinition> parseCreateTableFunctionBody() throws SyntaxException {
        assertTokenType(Tokens.operatorType);
        assertTokenBody("(");
        nextToken();

        List<AstColumnDefinition> columnDefinitionList = new ArrayList<>();
        for (;;) {
            //todo
        }
    }

    private AstColumnDefinition parseColumnDefinition() throws SyntaxException {
        AstFieldName fieldName = parseFieldName();
        AstType type = parseType();
        AstConstraint constraint = parseConstraint();
        return new AstColumnDefinition(fieldName, type, constraint);
    }

    private AstConstraint parseConstraint() {
        //todo
        return null;
    }

    private AstType parseType() throws SyntaxException {
        assertTokenType(Tokens.identifierType);
        AstType type = new AstType(currentToken.getBody());
        nextToken();
        return type;
    }

    private AstFieldName parseFieldName() throws SyntaxException {
        assertTokenType(Tokens.identifierType);
        AstFieldName fieldName = new AstFieldName(currentToken.getBody());
        nextToken();
        return fieldName;
    }

    private AstTableName parseTableName() throws SyntaxException {
        assertTokenType(Tokens.identifierType);
        String first = currentToken.getBody();
        nextToken();
        if (currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
        && currentToken.getBody().equalsIgnoreCase(".")) {
            nextToken();
            assertTokenType(Tokens.identifierType);
            String second = currentToken.getBody();
            nextToken();
            return new AstTableName(first, second);
        } else {
            return new AstTableName("public", first);
        }
    }

    private boolean parseIfNotExistPrefix() throws SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.keywordType)
        && currentToken.getBody().equalsIgnoreCase("if")) {
            nextToken();
            assertTokenType(Tokens.keywordType);
            assertTokenBody("not");
            nextToken();
            assertTokenType(Tokens.keywordType);
            assertTokenBody("exists");
            nextToken();
            return true;
        } else {
            return false;
        }
    }

    private void assertTokenType(String type) throws SyntaxException {
        if (!currentToken.getType().equalsIgnoreCase(type)) {
            throw new SyntaxException(type, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private void assertTokenBody(String body) throws SyntaxException {
        if (!currentToken.getBody().equalsIgnoreCase(body)) {
            throw new SyntaxException(body, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }
}
