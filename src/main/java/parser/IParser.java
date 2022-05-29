package parser;

import parser.ast.AstProgram;

public interface IParser {
    //return ast-tree head
    //pass the input into parser inside
    AstProgram parse(String input);
}
