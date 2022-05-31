package parser;

import lexer.ILexer;
import lexer.Lexer;
import lexer.token.*;
import parser.ast.AstType;
import parser.ast.arithmetic.*;
import parser.ast.condition.AstCondition;
import parser.ast.condition.AstConditionConstantRValue;
import parser.ast.condition.AstConditionOperator;
import parser.ast.condition.AstConditionSeparator;
import parser.ast.constraint.*;
import parser.ast.function.AstFunction;
import parser.ast.AstProgram;
import parser.ast.function.AstColumnDefinition;
import parser.ast.function.data.*;
import parser.ast.function.index.AstCreateIndexFunction;
import parser.ast.function.index.AstDropIndexFunction;
import parser.ast.function.query.AstSelectFunction;
import parser.ast.function.table.AstAlterTableFunction;
import parser.ast.function.table.AstCreateTableFunction;
import parser.ast.function.table.AstDropTableFunction;
import parser.ast.function.table.alter.*;
import parser.ast.name.*;
import parser.ast.value.*;
import parser.exception.BadArithmeticExpressionException;
import parser.exception.BadConditionExpressionException;
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
    public AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        return parseProgram();
    }

    private AstProgram parseProgram() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        nextToken();
        AstProgram program = new AstProgram();

        for (;;) {
            AstFunction function = parseFunction();
            program.addFunction(function);

            assertTokenType(Tokens.operatorType);
            assertTokenBody(";");
            nextToken();

            if (currentToken.getType().equals(Tokens.eofType)) {
                break;
            }
        }

        int varForBreakpoint = 666;
        return program;
    }

    private AstFunction parseFunction() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        assertTokenType(Tokens.keywordType);

        if (currentToken.getBody().equalsIgnoreCase("create")) {
            return parseCreateTableFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("alter")) {
            return parseAlterTableFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("drop")) {
            return parseDropTableFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("insert")) {
            return parseInsertFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("update")) {
            return parseUpdateFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("delete")) {
            return parseDeleteFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("icreate")) {
            return parseCreateIndexFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("idrop")) {
            return parseDropIndexFunction();
        } else if (currentToken.getBody().equalsIgnoreCase("select")) {
            return parseSelectFunction();
        }  else {
            throw new SyntaxException("Function command", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstSelectFunction parseSelectFunction() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("select");
        nextToken();
        List<AstFieldName> columnList = parseColumnList();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("from");
        nextToken();
        AstTableName tableName = parseTableName();
//        assertTokenType(Tokens.keywordType); to add here
//        assertTokenBody("as");
//        nextToken();
        AstCondition condition = parseConditionPostfix();
        return new AstSelectFunction(columnList, tableName, condition);
    }

    private AstDropIndexFunction parseDropIndexFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("idrop");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("index");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstIndexName indexName = parseIndexName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("from");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("table");
        nextToken();
        AstTableName tableName = parseTableName();
        return new AstDropIndexFunction(hasIfExistPrefix, indexName, tableName);
    }

    private AstCreateIndexFunction parseCreateIndexFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("icreate");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("index");
        nextToken();
        boolean hasIfNotExistPrefix = parseIfNotExistPrefix();
        AstIndexName indexName = parseIndexName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("on");
        nextToken();
        AstTableName tableName = parseTableName();
        assertTokenType(Tokens.operatorType);
        assertTokenBody("(");
        nextToken();
        AstFieldName fieldName = parseFieldName();
        assertTokenType(Tokens.operatorType);
        assertTokenBody(")");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("using");
        nextToken();
        AstIndexType indexType = parseIndexType();
        return new AstCreateIndexFunction(hasIfNotExistPrefix, indexName, tableName, fieldName, indexType);
    }

    private AstDeleteFunction parseDeleteFunction() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("delete");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("from");
        nextToken();
        AstTableName tableName = parseTableName();
        AstCondition condition = parseConditionPostfix();
        return new AstDeleteFunction(tableName, condition);
    }

    private AstUpdateFunction parseUpdateFunction() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("update");
        nextToken();
        AstTableName tableName = parseTableName();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("set");
        nextToken();
        List<AstUpdateValue> updateValueList = parseUpdateFunctionSetList();
        AstCondition condition = parseConditionPostfix();
        return new AstUpdateFunction(tableName, updateValueList, condition);
    }

    private AstCondition parseConditionPostfix() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("where");
        nextToken();
        return parseCondition();
    }

    private List<AstUpdateValue> parseUpdateFunctionSetList() throws SyntaxException {
        List<AstUpdateValue> updateValueList = new ArrayList<>();

        for (;;) {
            AstFieldName fieldName = parseFieldName();
            assertTokenType(Tokens.operatorType);
            assertTokenBody("=");
            nextToken();
            AstValue value = parseValue();
            updateValueList.add(new AstUpdateValue(fieldName, value));

            if (!currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                    || !currentToken.getBody().equalsIgnoreCase(",")) {
                break;
            }
            nextToken();
        }

        return updateValueList;
    }

    private AstInsertFunction parseInsertFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("insert");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("into");
        nextToken();
        AstTableName tableName = parseTableName();
        assertTokenType(Tokens.operatorType);
        assertTokenBody("(");
        nextToken();
        List<AstFieldName> columnList = parseColumnList();
        assertTokenType(Tokens.operatorType);
        assertTokenBody(")");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("values");
        nextToken();
        List<AstInsertRow> rowList = parseInsertFunctionBody(columnList.size());
        return new AstInsertFunction(tableName, columnList, rowList);
    }

    private List<AstInsertRow> parseInsertFunctionBody(int numberOfFields) throws SyntaxException {
        List<AstInsertRow> rowList = new ArrayList<>();

        for (;;) {
            assertTokenType(Tokens.operatorType);
            assertTokenBody("(");
            nextToken();

            AstInsertRow row = new AstInsertRow(parseValueList());

            assertTokenType(Tokens.operatorType);
            assertTokenBody(")");
            nextToken();

            if (!currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                    || !currentToken.getBody().equalsIgnoreCase(",")) {
                break;
            }
            nextToken();
        }

        return rowList;
    }

    private List<AstValue> parseValueList(int numberOfFields) throws SyntaxException {
        List<AstValue> valueList = new ArrayList<>();

        for (int i = 0; i < numberOfFields; i++) {
            valueList.add(parseValue());
            nextToken();
        }

        return valueList;
    }

    private List<AstValue> parseValueList() throws SyntaxException {
        List<AstValue> valueList = new ArrayList<>();

        for (;;) {
            valueList.add(parseValue());

            if (!currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                    || !currentToken.getBody().equalsIgnoreCase(",")) {
                break;
            }
            nextToken();
        }

        return valueList;
    }

    private List<AstFieldName> parseColumnList() throws SyntaxException {
        List<AstFieldName> columnList = new ArrayList<>();

        for (;;) {
            columnList.add(parseFieldName());

            if (!currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                    || !currentToken.getBody().equalsIgnoreCase(",")) {
                break;
            }
            nextToken();
        }

        return columnList;
    }

    private AstDropTableFunction parseDropTableFunction() throws SyntaxException {
        assertTokenType(Tokens.keywordType);
        assertTokenBody("drop");
        nextToken();
        assertTokenType(Tokens.keywordType);
        assertTokenBody("table");
        nextToken();
        boolean hasIfExistPrefix = parseIfExistPrefix();
        AstTableName tableName = parseTableName();
        return new AstDropTableFunction(hasIfExistPrefix, tableName);
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
            StringToken stringToken = (StringToken) currentToken;
            nextToken();
            return new AstStringValue(stringToken.getBody());
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

    //todo check
    private AstCondition parseCondition() throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException {
        AstCondition condition = new AstCondition();
        parseConditionHead(condition);
        condition.emptyStack();
        return condition;
    }

    private void parseConditionHead(AstCondition condition) throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.keywordType)
                && currentToken.getBody().equalsIgnoreCase("not")) {
            condition.addPart(new AstConditionOperator("!"));
            nextToken();
        }

        parseConditionAndBlock(condition);
    }

    private void parseConditionAndBlock(AstCondition condition) throws BadArithmeticExpressionException, SyntaxException, BadConditionExpressionException {
        parseConditionOrBlock(condition);

        if (currentToken.getType().equalsIgnoreCase(Tokens.keywordType)
                && currentToken.getBody().equalsIgnoreCase("and")) {
            condition.addPart(new AstConditionOperator("&&"));
            nextToken();
            parseConditionAndBlock(condition);
        }
    }

    private void parseConditionOrBlock(AstCondition condition) throws BadArithmeticExpressionException, SyntaxException, BadConditionExpressionException {
        parseConditionComparisonBlock(condition);

        if (currentToken.getType().equalsIgnoreCase(Tokens.keywordType)
                && currentToken.getBody().equalsIgnoreCase("or")) {
            condition.addPart(new AstConditionOperator("||"));
            nextToken();
            parseConditionOrBlock(condition);
        }
    }

    private void parseConditionComparisonBlock(AstCondition condition) throws BadArithmeticExpressionException, SyntaxException, BadConditionExpressionException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                && currentToken.getBody().equalsIgnoreCase("(")) {
            condition.addPart(new AstConditionSeparator("SEPARATOR_OPEN"));
            nextToken();
            parseConditionHead(condition);
            assertTokenType(Tokens.operatorType);
            assertTokenBody(")");
            nextToken();
            condition.addPart(new AstConditionSeparator("SEPARATOR_CLOSE"));
        } else {
            AstArithExpr first = parseArithExpr();
            condition.addPart(new AstConditionConstantRValue(first));
            assertTokenType(Tokens.operatorType);

            if (currentToken.getBody().equalsIgnoreCase(">")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else if (currentToken.getBody().equalsIgnoreCase("<")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else if (currentToken.getBody().equalsIgnoreCase("=")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else if (currentToken.getBody().equalsIgnoreCase(">=")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else if (currentToken.getBody().equalsIgnoreCase("<=")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else if (currentToken.getBody().equalsIgnoreCase("!=")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
            } else {
                throw new SyntaxException("Comparison operator", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
            }

            nextToken();
            AstArithExpr second = parseArithExpr();
            condition.addPart(new AstConditionConstantRValue(second));
        }
    }

    private boolean isCompareOperator(String op) {
        return (op.equalsIgnoreCase("==") || op.equalsIgnoreCase("<=")
        || op.equalsIgnoreCase(">=") || op.equalsIgnoreCase("!=")
        || op.equalsIgnoreCase("<") || op.equalsIgnoreCase(">"));
    }

    //todo check
    private AstArithExpr parseArithExpr() throws BadArithmeticExpressionException, SyntaxException {
        AstArithExpr arithExpr = new AstArithExpr();
        parseArithExprHead(arithExpr);
        arithExpr.emptyStack();
        return arithExpr;
    }

    private void parseArithExprHead(AstArithExpr arithExpr) throws BadArithmeticExpressionException, SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.operatorType)
                && currentToken.getBody().equalsIgnoreCase("-")) {
            arithExpr.addPart(new AstArithExprOperator('-', 1));
            nextToken();
        }
        parseArithExprBody(arithExpr);
    }

    private void parseArithExprBody(AstArithExpr arithExpr) throws BadArithmeticExpressionException, SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.stringType)) {
            //string
            AstValue value = parseValue();
            arithExpr.addPart(new AstArithExprValue(value));
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.symbolType)) {
            //symbol
            AstValue value = parseValue();
            arithExpr.addPart(new AstArithExprValue(value));
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.integerNumberType)) {
            //integer and long
            AstValue value = parseValue();
            arithExpr.addPart(new AstArithExprValue(value));
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.floatingNumberType)) {
            //double
            AstValue value = parseValue();
            arithExpr.addPart(new AstArithExprValue(value));
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.identifierType)) {
            //field reference
            AstFieldReference fieldReference = parseFieldReference();
            arithExpr.addPart(new AstArithExprIdentConstant(fieldReference));
        } else if (currentToken.getType().equalsIgnoreCase(Tokens.operatorType)) {
            if (currentToken.getBody().equalsIgnoreCase("[")) {
                // <(> arith_expr <)>
                arithExpr.addPart(new AstArithExprSeparator("SEPARATOR_OPEN"));
                nextToken();
                parseArithExprHead(arithExpr);
                assertTokenType(Tokens.operatorType);
                assertTokenBody("]");
                arithExpr.addPart(new AstArithExprSeparator("SEPARATOR_CLOSE"));
                nextToken();
            } else {
                throw new SyntaxException("Arithmetic expression part", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
            }
        } else {
            throw new SyntaxException("Arithmetic expression part", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }

        parseArithExprTail(arithExpr);
    }

    private void parseArithExprTail(AstArithExpr arithExpr) throws BadArithmeticExpressionException, SyntaxException {
        if (currentToken.getType().equalsIgnoreCase(Tokens.operatorType)) {
            //binary op </> | <*> | <+> | <->.
            if (currentToken.getBody().equalsIgnoreCase("*")) {
                arithExpr.addPart(new AstArithExprOperator('*', 2));
                nextToken();
                parseArithExprHead(arithExpr);
            } else if (currentToken.getBody().equalsIgnoreCase("/")) {
                arithExpr.addPart(new AstArithExprOperator('/', 2));
                nextToken();
                parseArithExprHead(arithExpr);
            } else if (currentToken.getBody().equalsIgnoreCase("+")) {
                arithExpr.addPart(new AstArithExprOperator('+', 2));
                nextToken();
                parseArithExprHead(arithExpr);
            } else if (currentToken.getBody().equalsIgnoreCase("-")) {
                arithExpr.addPart(new AstArithExprOperator('-', 2));
                nextToken();
                parseArithExprHead(arithExpr);
            }
        }
    }

    private AstType parseType() throws SyntaxException {
        assertTokenType(Tokens.keywordType);

        if (currentToken.getBody().equalsIgnoreCase("int")
        || currentToken.getBody().equalsIgnoreCase("serial")
        || currentToken.getBody().equalsIgnoreCase("long")
        || currentToken.getBody().equalsIgnoreCase("bigserial")
        || currentToken.getBody().equalsIgnoreCase("double")
        || currentToken.getBody().equalsIgnoreCase("string")
        || currentToken.getBody().equalsIgnoreCase("char")) {
            AstType type = new AstType(currentToken.getBody());
            nextToken();
            return type;
        } else {
            throw new SyntaxException("Type", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstIndexType parseIndexType() throws SyntaxException {
        assertTokenType(Tokens.keywordType);

        if (currentToken.getBody().equalsIgnoreCase("btree")
        || currentToken.getBody().equalsIgnoreCase("hash")) {
            AstIndexType type = new AstIndexType(currentToken.getBody());
            nextToken();
            return type;
        } else {
            throw new SyntaxException("Index type", currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstFieldName parseFieldName() throws SyntaxException {
        assertTokenType(Tokens.identifierType);
        AstFieldName fieldName = new AstFieldName(currentToken.getBody());
        nextToken();
        return fieldName;
    }

    private AstFieldReference parseFieldReference() throws SyntaxException {
        return new AstFieldReference(parseFieldName());
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
