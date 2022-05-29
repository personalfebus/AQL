package parser.ast.function.table;

import parser.ast.function.AstFunction;

//todo
public class CreateTableFunction implements AstFunction {

    @Override
    public String getType() {
        return CreateTableFunction.class.getName();
    }
}
