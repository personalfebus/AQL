package parser.ast.function.table;

import parser.ast.function.AstFunction;

//todo
public class DropTableFunction implements AstFunction {

    @Override
    public String getType() {
        return DropTableFunction.class.getName();
    }
}
