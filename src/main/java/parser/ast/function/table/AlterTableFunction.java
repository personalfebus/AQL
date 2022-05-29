package parser.ast.function.table;

import parser.ast.function.AstFunction;

//todo
public class AlterTableFunction implements AstFunction {

    @Override
    public String getType() {
        return AlterTableFunction.class.getName();
    }
}