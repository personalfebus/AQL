package parser.ast.function.data;

import parser.ast.function.AstFunction;

//todo
public class DeleteFunction implements AstFunction {

    @Override
    public String getType() {
        return DeleteFunction.class.getName();
    }
}
