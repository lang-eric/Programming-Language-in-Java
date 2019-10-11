/* This is the Jott Tokenizer, it will take the entire input as one string and tokenize is accordingly
 * '+' - plus
 * '-' - minus
 * '*' - mult
 * '\' - divide
 * '^' - power
 * ',' - comma
 * ';' - end_stmt
 * '(' - start_paren
 * ')' - end_paren
 * '=' - assign
 * 'String' - type_String
 * 'Integer' - type_Integer
 * 'Double' - type_Double
 * 'print(' - print
 * 'charAt(' - charAt
 * 'concat(' - concat
 * A set of letters and numbers that begins with an uppercase letter- upper_keyword
 * A set of letters and numbers that begins with a lowercase letter- lower_keyword
 * A set of letters, numbers, and spaces surrounded by quoation marks ('"') - string
 *
 * Author: Justin Kolodny
 */

import java.util.ArrayList;



public class JottTokenizer {
    public static class Token{
        String type;
        String value;

        public String getType() {
            return type;
        }

        public String getValue() {
            StringBuilder string_value = new StringBuilder();
            for(int count=0;count<value.length();count++){
                    string_value.append(value.charAt(count));
                }
            return string_value.toString();
        }

        public Token(String type, String value){
            this.type=type;
            this.value=value;
        }

    }




    public static ArrayList<Token> JottTokenizer(String args){
        ArrayList<Character> temp=new ArrayList<>();
        ArrayList<Token> Tokens = new ArrayList<>();
        char[] input=args.toCharArray();
        int length=input.length;
        int number_type=0;
        int end=0;
        int line_number=1;
        int character_count=0;
        for (int count=0;count<length;count++) {
            //System.out.println(line_number);
            if(length>count+2&&character_count==0){
                if(input[count]=='/'&&input[count+1]=='/'){
                    while(input[count]!='\n'){
                        count++;
                        if(count==length+1){
                            return Tokens;
                        }
                    }
                    line_number+=1;
                    character_count=0;
                }
            }
            number_type=0;
            character_count++;
            if (input[count]=='\n'){
                line_number++;
                character_count=0;
                continue;
            }
            if (input[count]== ' '){
                continue;
            }
            if (input[count] == '+') {
                Tokens.add(new Token("plus","+"));
                continue;
            }
            else if (input[count] == '-') {
                Tokens.add(new Token("minus","-"));
                continue;
            }
            else if (input[count] == '*') {
                Tokens.add(new Token("mult","*"));
                continue;
            }
            else if (input[count] == '/') {
                Tokens.add(new Token("divide","/"));
                continue;
            }
            else if (input[count] == '^') {
                Tokens.add(new Token("power","^"));
                continue;
            }
            else if (input[count] == ')') {
                Tokens.add(new Token("end_paren",")"));
                continue;
            }
            else if (input[count] == '(') {
                Tokens.add(new Token("start_paren","("));
                continue;
            }
            else if (input[count] == ';') {
                Tokens.add(new Token("end_stmt",";"));
                continue;
            }
            else if (input[count] == '=') {
                Tokens.add(new Token("assign","="));
                continue;
            }
            else if (input[count] == ',') {
                Tokens.add(new Token("comma", ","));
                continue;
            }
            String inputString = new String(input); //convert the char[] into a string, for readable comparison's sake.
            if(count+5<length) {
                if (inputString.toString().substring(count, count+6).equals("String")){
                    Tokens.add(new Token("type_String","String"));
                    count+=5;
                    character_count+=5;
                    continue;
                }
                else if (inputString.toString().substring(count, count+6).equals("Double")){
                    Tokens.add(new Token("type_Double","Double"));
                    count+=5;
                    character_count+=5;
                    continue;
                }
                else if (inputString.substring(count, count+6).equals("print(")) {
                    Tokens.add(new Token("print","print("));
                    count+=5;
                    character_count+=5;
                    continue;
                }
            }
            if(count+6<length) {
                if (inputString.substring(count, count+7).equals("Integer")) {
                    Tokens.add(new Token("type_Integer","Integer"));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                else if (inputString.substring(count, count+7).equals("concat(")) {
                    Tokens.add(new Token("concat","concat("));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                else if (inputString.substring(count, count+7).equals("charAt(")) {
                    Tokens.add(new Token("charAt","charAt("));
                    count+=6;
                    character_count+=6;
                    continue;
                }
            }

            while(Character.isDigit(input[count]) || input[count]=='.') {
                end=1;
                if(input[count]=='.') {
                    if(number_type==1) {
                        System.out.println(("Syntax error: two '.'s in a row " +
                                "does not make a number at "+line_number + ", character "+character_count));
                        System.exit(-1);
                        break;
                    }
                    number_type=1;
                }
                temp.add(input[count]);
                count++;
                character_count++;
                if(count!=length){
                    if (input[count]=='\n'){
                        line_number++;
                        character_count=0;
                    }
                }
                if(count==length) {
                    if(number_type==1) {
                        Tokens.add(new Token("double",temp.toString()));
                    }
                    else {
                        Tokens.add(new Token("integer", temp.toString()));
                    }
                    temp.removeAll(temp);
                    break;
                }
                if(!(Character.isDigit(input[count])) && !(input[count]=='.')) {

                    if(number_type==1) {
                        Tokens.add(new Token("double",temp.toString()));
                    }
                    else {
                        Tokens.add(new Token("integer", temp.toString()));
                    }
                    if (input[count] == ')') {
                        Tokens.add(new Token("end_paren", ")"));
                    }
                    temp.removeAll(temp);
                    break;
                }
            }
            if(end==1){
                end=0;
                continue;
            }
            if (Character.isLowerCase(input[count] )) {
                while(Character.isLowerCase(input[count] )||
                        Character.isUpperCase(input[count] )
                        ||Character.isDigit(input[count])){
                        temp.add(input[count]);
                        count++;
                        character_count++;
                    if(count!=length){
                        if (input[count]=='\n'){
                            line_number++;
                            character_count=0;
                            continue;
                        }
                    }
                    if(count==length) {
                        Tokens.add(new Token("lower_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                    if(!(Character.isLowerCase(input[count]))&&
                            !((Character.isUpperCase(input[count]))
                            &&!(Character.isDigit(input[count])))){
                        Tokens.add(new Token("lower_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                }
                continue;
            }
            if (Character.isUpperCase(input[count])) {
                while(Character.isLowerCase(input[count])||
                        (Character.isUpperCase(input[count])
                        ||Character.isDigit(input[count]))){
                    temp.add(input[count]);
                    count++;
                    character_count++;
                    if(count!=length){
                        if (input[count]=='\n'){
                            line_number++;
                            character_count=0;
                        }
                    }
                    if(count==length) {
                        Tokens.add(new Token("upper_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                    if(!(Character.isLowerCase(input[count])&&
                            !(Character.isUpperCase(input[count]))
                            &&!(Character.isDigit(input[count])))){
                        Tokens.add(new Token("upper_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                }
                continue;
            }

            while (input[count] == '"') {
                count++;
                character_count++;
                if(count!=length){
                    if (input[count]=='\n'){
                        line_number++;
                        character_count=0;
                    }
                }
                while(Character.isLowerCase(input[count])||
                        (Character.isUpperCase(input[count]))
                        ||Character.isDigit(input[count])
                        || input[count] == ' '){
                    temp.add(input[count]);
                    count++;
                    character_count++;
                    if(count!=length){
                        if (input[count]=='\n'){
                            line_number++;
                            character_count=0;
                        }
                    }
                    if(count==length){
                        System.out.println(("Syntax error: missing '\"' at line "+line_number + ", character "+character_count));
                        System.exit(-1);
                    }
                }
                if(input[count]=='"'){
                    Tokens.add(new Token("string", temp.toString()));
                    temp.removeAll(temp);
                    end=1;
                    break;
                }
                else{
                    System.out.println(("Syntax error: missing '\"' at line "+line_number + ", character "+character_count));
                    System.exit(-1);
                }
            }
            if(end==1){
                end=0;
                continue;
            }
            System.out.println(("Syntax error: Can not identitfy "+input[count]+
                    " at line "+line_number + ", character "+character_count));
            System.exit(-1);
        }
        return Tokens;

}//Hello
    
}