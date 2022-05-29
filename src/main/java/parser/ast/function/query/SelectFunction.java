package parser.ast.function.query;

import parser.ast.function.AstFunction;

//todo
public class SelectFunction implements AstFunction {

    @Override
    public String getType() {
        return SelectFunction.class.getName();
    }
}
