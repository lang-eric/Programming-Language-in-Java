
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JottRunner {
    public static ArrayList<String> line_list;
    public static void Jottrunner(String file_name) {
        try {
            InputStream is = new FileInputStream(file_name);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            line_list=new ArrayList<>();
            while(line != null){
                line_list.add(line);
                sb.append(line);
                sb.append('\n');
                line = buf.readLine();
            }
            String fileAsString = sb.toString();
            ArrayList<JottTokenizer.Token> TokenList;
            TokenList= JottTokenizer.JottTokenizer(fileAsString);
//            for (JottTokenizer.Token token: TokenList) {
//                System.out.println("Token Type: "+token.getType()+" Token Value: "+token.getValue());
//            }

            ParseTreeNode tree = JottParser.parseTokens(TokenList, file_name);
            List<String> out = JottEvaluation.JottEvaluation(tree);
            PrintStream output = new PrintStream(new FileOutputStream(file_name.substring(0, file_name.length() - 1) + "out", true));
            for (String s : out) {
                output.println(s);
            }
            output.close();


//            for(String out: output) {
//                System.out.println(out);
//            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error, invalid file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
