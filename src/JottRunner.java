
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JottRunner {
    public static void Jottrunner(String file_name) {
        try {
            InputStream is = new FileInputStream(file_name);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();

            StringBuilder sb = new StringBuilder();
            while(line != null){
                sb.append(line);
                sb.append('\n');
                line = buf.readLine();
            }
            String fileAsString = sb.toString();
            ArrayList<JottTokenizer.Token> TokenList;
            TokenList= JottTokenizer.JottTokenizer(fileAsString);
            for (JottTokenizer.Token token: TokenList) {
                System.out.println("Token Type: "+token.getType()+" Token Value: "+token.getValue());
            }

            ParseTreeNode tree = JottParser.parseTokens(TokenList);
            List<String> out = JottEvaluation.JottEvaluation(tree);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error, invalid file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
