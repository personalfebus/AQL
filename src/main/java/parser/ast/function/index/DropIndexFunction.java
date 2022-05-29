package parser.ast.function.index;

import parser.ast.function.AstFunction;

//todo
public class DropIndexFunction implements AstFunction {

    @Override
    public String getType() {
        return DropIndexFunction.class.getName();
    }
}
