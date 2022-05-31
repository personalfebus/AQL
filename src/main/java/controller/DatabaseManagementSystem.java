package controller;

import database.field.*;
import parser.IParser;
import parser.Parser;
import parser.ast.AstProgram;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Контроллер СУБД
 */
public class DatabaseManagementSystem {
    public static void main(String[] args) throws IOException {
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

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = in.readLine();

        while (input != null) {
            Path fileName = Path.of("queries/" + input);
            String queries = Files.readString(fileName);
            IParser parser = new Parser(queries);
            AstProgram program = parser.parse();
            //step to the next input file name
            input = in.readLine();
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
    }
}
