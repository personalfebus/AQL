package controller;

import database.Database;
import database.field.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.IParser;
import parser.Parser;
import parser.ast.AstProgram;
import parser.exception.BadArithmeticExpressionException;
import parser.exception.BadConditionExpressionException;
import parser.exception.SyntaxException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Контроллер СУБД
 */
public class DatabaseManagementSystem {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManagementSystem.class.getName());

    public static void main(String[] args) {
        Database database = new Database();
        System.out.println("======= Good to work =======");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        try {
            input = in.readLine();
        } catch (IOException e) {
            log.error("Error with stdin",e);
        }

        while (!Objects.equals(input, "shutdown")) {
            Path fileName = Path.of("queries/" + input);

            try {
                String queries = Files.readString(fileName);
                IParser parser = new Parser(queries);
                AstProgram program = parser.parse();
                program.execute(database);
            }  catch (SyntaxException | BadArithmeticExpressionException | BadConditionExpressionException e) {
                log.error("Error while parsing queries", e);
            } catch (IOException e) {
                log.error("Could not find file", e);
            }

            //step to the next input file name
            input = null;

            while (input == null) {
                try {
                    input = in.readLine();
                } catch (IOException e) {
                    log.error("Error with reading next command from stdin", e);
                }
            }
        }


        //tests
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("binaries/output1.txt");
            Ror ror = new Ror(1);
            ror.setName("first");
            List<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(9);
            ror.setNeighbours(list);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ror);

            FileInputStream fileInputStream = new FileInputStream("binaries/output1.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Ror r = (Ror) objectInputStream.readObject();
            System.out.println(r);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Field> fields = new ArrayList<>();
        fields.add(new IntField(5));
        fields.add(new DoubleField(5.01d));
        fields.add(new LongField(1L));
        fields.sort(Field::compareTo);
        System.out.println(fields);
        Field[][] fields1 = new Field[2][4];
        fields1[1][2] = new LongField(5L);
        System.out.println(Arrays.deepToString(fields1));

        System.out.println(Double.parseDouble("3.14"));
    }
}
