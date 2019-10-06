import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JottRunner {
    public static void main(String[] args) {
        try {
            File code = new File(args[0]); //Java:java Jott program.j eg: args[0] --> program.j
            Scanner s = new Scanner(code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error, invalid file.");
        }


    }

}
