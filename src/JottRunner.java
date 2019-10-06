import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Scanner;

public class JottRunner {
    public static void main(String[] args) {
        try {
            args = new String[1]; //hacky stupid way to test without actually programming the args into the the stupid run configuration.
            args[0] = "src/test.j"; //hacky stupid way to test without actually programming the args into the the stupid run configuration.

            File code = new File(args[0]); //Java:java Jott program.j eg: args[0] --> program.j
            Scanner s = new Scanner(code);
            while(s.hasNextLine()) {
                /*processing of each line happens here*/
                System.out.println(s.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error, invalid file.");
        }


    }

}
