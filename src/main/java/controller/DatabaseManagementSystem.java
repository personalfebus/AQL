package controller;

import parser.IParser;
import parser.Parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер СУБД
 */
public class DatabaseManagementSystem {
    //todo add gradle logger dependency

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = in.readLine();

        while (input != null) {
            Path fileName = Path.of("queries/" + input);
            String queries = Files.readString(fileName);
            IParser parser = new Parser(queries);

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
