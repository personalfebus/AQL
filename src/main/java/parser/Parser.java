package parser;

import lexer.ILexer;
import lexer.Lexer;
import lexer.token.*;
import parser.ast.AstType;
import parser.ast.constraint.*;
import parser.ast.function.AstFunction;
import parser.ast.AstProgram;
import parser.ast.function.AstColumnDefinition;
import parser.ast.function.table.AstAlterTableFunction;
import parser.ast.function.table.AstCreateTableFunction;
import parser.ast.function.table.alter.*;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstIndexName;
import parser.ast.name.AstTableName;
import parser.ast.value.*;
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
        } else if (currentToken.getBody().equalsIgnoreCase("alter")) {
            return parseAlterTableFunction();
        }

        return null;
    }

    private AstAlterTableFunction parseAlterTableFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("alter");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("table");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstTableName tableName = parseTableName();
        AstAlterTableFunctionBody body = parseAlterTableFunctionBody();
        return new AstAlterTableFunction(hasIfExistPrefix, tableName, body);
    }

    private AstAlterTableFunctionBody parseAlterTableFunctionBody() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        if (currentToken.getBody().equalsIgnoreCase("add")) {
            return parseAddColumnFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("drop")) {
            return parseDropColumnFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("rename")) {
            return parseRenameFunction();
        } else {
            throw new SyntaxException("Alter table command", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstRenameFunction parseRenameFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("rename");
        nextToken();

        assertTokenType(Tokens.keywordType);
        if (currentToken.getBody().equalsIgnoreCase("column")) {
            return parseRenameColumnFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("table")) {
            return parseRenameTableFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("index")) {
            return parseRenameIndexFunction();
        } else {
            throw new SyntaxException("Alter table rename command", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstRenameIndexFunction parseRenameIndexFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("index");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstIndexName oldName = parseIndexName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("to");
        nextToken();
        AstIndexName newName = parseIndexName();
        return new AstRenameIndexFunction(hasIfExistPrefix, oldName, newName);
    }

    private AstRenameTableFunction parseRenameTableFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("table");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstTableName oldName = parseTableName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("to");
        nextToken();
        AstTableName newName = parseTableName();
        return new AstRenameTableFunction(hasIfExistPrefix, oldName, newName);
    }

    private AstRenameColumnFunction parseRenameColumnFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("column");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstFieldName oldField = parseFieldName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("to");
        nextToken();
        AstFieldName newField = parseFieldName();
        return new AstRenameColumnFunction(hasIfExistPrefix, oldField, newField);
    }

    private AstDropColumnFunction parseDropColumnFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("drop");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("column");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstFieldName fieldName = parseFieldName();
        return new AstDropColumnFunction(hasIfExistPrefix, fieldName);
    }

    private AstAddColumnFunction parseAddColumnFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("add");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("column");
        nextToken();
        boolean hasIfNotExistPrefix = parseIfNotExistPrefix();
        AstColumnDefinition columnDefinition = parseColumnDefinition();
        return new AstAddColumnFunction(hasIfNotExistPrefix, columnDefinition);
    }

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
        return new AstCreateTableFunction(hasIfNotExistPrefix, tableName, columnDefinitionList);
    }

    private List<AstColumnDefinition> parseCreateTableFunctionBody() throws SyntaxException {
        assertTokenType(Tokens.operatorType);
        assertTokenBody("(");
        nextToken();

        List<AstColumnDefinition> columnDefinitionList = new ArrayList<>();
        for (;;) {
            AstColumnDefinition columnDefinition = parseColumnDefinition();
            columnDefinitionList.add(columnDefinition);

            assertTokenType(Tokens.operatorType);
            if (currentToken.getBody().equalsIgnoreCase(")")) {
                break;
            }

            assertTokenBody(",");
            nextToken();
        }

        assertTokenType(Tokens.operatorType);
        assertTokenBody(")");
        nextToken();
        return columnDefinitionList;
    }

    private AstColumnDefinition parseColumnDefinition() throws SyntaxException {
        AstFieldName fieldName = parseFieldName();
        AstType type = parseType();
        AstConstraint constraint = parseConstraint();
        return new AstColumnDefinition(fieldName, type, constraint);
    }

    private AstConstraint parseConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        if (currentToken.getBody().equalsIgnoreCase("primary")) {
            return parsePrimaryKeyConstraint();
        } else if (currentToken.getBody().equalsIgnoreCase("foreign")) {
            return parseForeignKeyConstraint();
        } else if (currentToken.getBody().equalsIgnoreCase("unique")) {
            return parseUniqueConstraint();
        } else if (currentToken.getBody().equalsIgnoreCase("not")) {
            return parseNotNullConstraint();
        } else if (currentToken.getBody().equalsIgnoreCase("default")) {
            return parseDefaultConstraint();
        } else {
            throw new SyntaxException("Constraint", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstDefaultConstraint parseDefaultConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("default");
        nextToken();
        AstValue value = parseValue();
        return new AstDefaultConstraint(value);
    }

    private AstValue parseValue() throws SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.stringType)) {
            nextToken();
            return new AstStringValue(currentToken.getBody());
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.symbolType)) {
            SymbolToken symbolToken = (SymbolToken) currentToken;
            nextToken();
            return new AstSymbolValue(symbolToken.getChar());
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.floatingNumberType)) {
            FloatingNumberToken floatingNumberToken = (FloatingNumberToken) currentToken;
            nextToken();
            return new AstFloatingNumberValue(floatingNumberToken.getNumber());
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.integerNumberType)) {
            IntegerNumberToken integerNumberToken = (IntegerNumberToken) currentToken;
            nextToken();
            return new AstIntegerNumberValue(integerNumberToken.getNumber());
        } else {
            throw new SyntaxException("Value", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstNotNullConstraint parseNotNullConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("not");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("null");
        nextToken();
        return new AstNotNullConstraint();
    }

    private AstUniqueConstraint parseUniqueConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("unique");
        nextToken();
        return new AstUniqueConstraint();
    }

    private AstForeignKeyConstraint parseForeignKeyConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("foreign");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("key");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("references");
        nextToken();
        AstTableName tableName = parseTableName();
        assertTokenType(Tokens.operatorType);
        assertTokenBody("(");
        nextToken();
        AstFieldName fieldName = parseFieldName();
        assertTokenType(Tokens.operatorType);
        assertTokenBody(")");
        nextToken();
        return new AstForeignKeyConstraint(tableName, fieldName);
    }

    private AstPrimaryKeyConstraint parsePrimaryKeyConstraint() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("primary");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("key");
        nextToken();
        return new AstPrimaryKeyConstraint();
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

    private AstIndexName parseIndexName() throws SyntaxException {
        assertTokenType(Tokens.identifierType);
        AstIndexName indexName = new AstIndexName(currentToken.getBody());
        nextToken();
        return indexName;
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

    private boolean parseIfExistPrefix() throws SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.keywordType)
                && currentToken.getBody().equalsIgnoreCase("if")) {
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
