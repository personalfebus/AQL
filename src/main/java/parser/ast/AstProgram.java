package parser.ast;

import database.Database;
import parser.ast.function.AstFunction;

import java.util.ArrayList;
import java.util.List;

public class AstProgram {
    private final List<AstFunction> functions;

    public AstProgram() {
        functions = new ArrayList<>();
    }

    public AstProgram(List<AstFunction> functions) {
        this.functions = functions;
    }

    public void addFunction(AstFunction function) {
        functions.add(function);
    }

    public List<AstFunction> getFunctions() {
        return functions;
    }

    public void execute(Database database) {
        for (AstFunction function : functions) {
            function.execute(database);
        }
    }
}
