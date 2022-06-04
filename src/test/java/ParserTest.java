import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.ast.AstProgram;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    public void testParser() throws Exception {
        Path filePath = Path.of("src/test/resources/test.aql");
        String input = new String(Files.readAllBytes(filePath));
        Parser parser = new Parser(input);
        AstProgram program = parser.parse();
        assertNotNull(program);
    }
}