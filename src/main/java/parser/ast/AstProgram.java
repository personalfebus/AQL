package parser.ast;

import parser.ast.function.AstFunction;

import java.util.HashSet;
import java.util.Set;

public class AstProgram {
    private final Set<AstFunction> functions;

    public AstProgram() {
        functions = new HashSet<>();
    }

    public AstProgram(Set<AstFunction> functions) {
        this.functions = functions;
    }

    public void addFunction(AstFunction function) {
        functions.add(function);
    }
}
