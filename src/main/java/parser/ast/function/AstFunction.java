package parser.ast.function;

import database.Database;

public interface AstFunction {
    String getType();
    void execute(Database database);
}
