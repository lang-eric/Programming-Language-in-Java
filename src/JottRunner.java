import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JottRunner {
    public static void main(String[] args) {
        try {
            args = new String[1]; //hacky stupid way to test without actually programming the args into the the stupid run configuration.
            args[0] = "src/test.j"; //hacky stupid way to test without actually programming the args into the the stupid run configuration.

            File code = new File(args[0]); //Java:java Jott program.j eg: args[0] --> program.j
            Scanner s = new Scanner(code);
            while (s.hasNextLine()) {
                /*processing of each line happens here*/
                String nextLine = s.nextLine();
                if (nextLine.replace(" ", "").substring(0, 2) != "//") { //remove whitespace from string, and only send to tokenizer if it isn't a comment.
                    replace_this_with_what_sends_the_line_to_the_tokenizer(nextLine);
                } else {  //print a string about receiving a comment; but really, else do nothing, because it must've been a comment, which we don't need.
                    System.out.println("+++++++++   Comment Received: " + nextLine);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error, invalid file.");
        }
    }

    static void replace_this_with_what_sends_the_line_to_the_tokenizer(String line) {
        System.out.println("+++++++++   Code sent to tokenizer: " + line);
    }

}
