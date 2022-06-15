package controller;

import database.Database;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.field.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.IParser;
import parser.Parser;
import parser.ast.AstProgram;
import parser.exception.BadArithmeticExpressionException;
import parser.exception.BadConditionExpressionException;
import parser.exception.SyntaxException;

import javax.xml.crypto.Data;
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
        Database currentDatabase = null;
        int mode = 0; //0 - new/checkout database; 1 - database operations
        System.out.println("======= Good to work =======");
        System.out.println("======= Now enter database management commands =======");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        try {
            input = in.readLine();
        } catch (IOException e) {
            log.error("Error with stdin",e);
        }

        while (!Objects.equals(input, "shutdown")) {
            if (mode == 0) {
                String[] commands = new String[0];
                if (input != null) {
                    commands = input.split(" ");
                }

                if (commands.length > 1) {
                    if (commands[0].equals("new")) {
                        try {
                            currentDatabase = new Database(commands[1]);
                            Database.writeToDisk(currentDatabase.getDatabaseUuid(), currentDatabase, currentDatabase.getPath());
                            mode = 1;
                            System.out.println("======= Now enter script names =======");
                        } catch (WriteToDiskError e) {
                            log.error("File system error", e);
                        }
                    } else if (commands[0].equals("checkout")) {
                        try {
                            currentDatabase = Database.readFromDisk(null, commands[1]);
                            mode = 1;
                            System.out.println("======= Now enter script names =======");
                        } catch (ReadFromDiskError e) {
                            log.error("File system error", e);
                        }
                    } else {
                        log.warn("Unknown command {}", input);
                    }
                } else {
                    log.warn("Unknown command {}", input);
                }
            } else {
                if (input.equals("exit")) {
                    currentDatabase = null;
                    mode = 0;
                    System.out.println("======= Now enter database management commands =======");
                } else if (input.equals("delete")) {
                    try {
                        currentDatabase.delete();
                    } catch (ReadFromDiskError e) {
                        log.error("Problems with file system", e);
                    }
                    mode = 0;
                    System.out.println("======= Now enter database management commands =======");
                } else {
                    Path fileName = Path.of("queries/" + input);

                    try {
                        String queries = Files.readString(fileName);
                        IParser parser = new Parser(queries);
                        AstProgram program = parser.parse();
                        program.execute(currentDatabase);
                    } catch (SyntaxException | BadArithmeticExpressionException | BadConditionExpressionException e) {
                        log.error("Error while parsing queries", e);
                    } catch (IOException e) {
                        log.error("Could not find file", e);
                    }
                }
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
            FileOutputStream fileOutputStream = new FileOutputStream("binaries/output1");
            Ror ror = new Ror(1);
            ror.setName("first");
            List<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(9);
            ror.setNeighbours(list);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ror);

            FileInputStream fileInputStream = new FileInputStream("binaries/output1");
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
