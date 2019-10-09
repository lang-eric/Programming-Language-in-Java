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

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        String value;
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
                Tokens.add(new Token("end_paren",")"));;
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
            else if (input[count] == ','){
                Tokens.add(new Token("comma",","));
                continue;
            }
            if(count+5<length) {
                if (input[count] == 'S' && input[count+1] == 't' && input[count+2] == 'r'
                        && input[count+3] == 'i' && input[count+4] == 'n' && input[count+5] == 'g') {
                    Tokens.add(new Token("type_String","String"));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                if (input[count] == 'D' && input[count+1] == 'o' && input[count+2] == 'u'
                        && input[count+3] == 'b' && input[count+4] == 'l' && input[count+5] == 'e') {
                    Tokens.add(new Token("type_Double","Double"));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                if (input[count] == 'p' && input[count + 1] == 'r' && input[count + 2] == 'i' &&
                        input[count + 3] == 'n' && input[count + 4] == 't' && input[count + 5] == '(') {
                    Tokens.add(new Token("print","print("));
                    count+=6;
                    character_count+=6;
                    continue;
                }
            }
            if(count+6<length) {
                if (input[count] == 'I' && input[count+1] == 'n' && input[count+2] == 't'
                        && input[count+3] == 'e' && input[count+4] == 'g' && input[count+5] == 'e'
                        && input[count+6] == 'r') {
                    Tokens.add(new Token("type_Integer","Integer"));
                    count+=7;
                    character_count+=7;
                    continue;
                }
                if (input[count] == 'c' && input[count+1] == 'o' && input[count+2] == 'n'
                        && input[count+3] == 'c' && input[count+4] == 'a' && input[count+5] == 't'
                        && input[count+6] == '(') {
                    Tokens.add(new Token("concat","concat("));
                    count+=7;
                    character_count+=7;
                    continue;
                }
                if(input[count] == 'c' && input[count+1] == 'h' && input[count+2] == 'a'
                        && input[count+3] == 'r' && input[count+4] == 'A' && input[count+5] == 't'
                        && input[count+6] == '(') {
                    Tokens.add(new Token("charAt","charAt("));
                    count+=7;
                    character_count+=7;
                    continue;
                }
            }

            while(input[count] >= 48 && input[count] <= 57||input[count]==46) {
                end=1;
                if(input[count]==46) {
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
                if(!(input[count] >= 48 && input[count] <= 57)&&!(input[count]==46)) {
                    if(number_type==1) {
                        Tokens.add(new Token("double",temp.toString()));
                    }
                    else {
                        Tokens.add(new Token("integer", temp.toString()));
                    }
                    temp.removeAll(temp);
                    break;
                }
            }
            if(end==1){
                end=0;
                continue;
            }
            if (input[count] >= 97 && input[count] <= 122) {
                while((input[count] >= 97 && input[count] <= 122)||
                        ((input[count])>=65 && input[count]<=90)
                        ||(input[count] >= 48 && input[count] <= 57)){
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
                    if(!(input[count] >= 97 && input[count] <= 122)&&
                            !((input[count])>=65 && input[count]<=90)
                            &&!(input[count] >= 48 && input[count] <= 57)) {
                        Tokens.add(new Token("lower_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                }
                continue;
            }
            if (input[count] >= 65 && input[count] <= 90) {
                while((input[count] >= 97 && input[count] <= 122)||
                        ((input[count])>=65 && input[count]<=90)
                        ||(input[count] >= 48 && input[count] <= 57)){
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
                    if(!(input[count] >= 97 && input[count] <= 122)&&
                            !((input[count])>=65 && input[count]<=90)
                            &&!(input[count] >= 48 && input[count] <= 57)) {
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
                while((input[count] >= 97 && input[count] <= 122)||
                        ((input[count])>=65 && input[count]<=90)
                        ||(input[count] >= 48 && input[count] <= 57)
                        || input[count] == 32){
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
                    break;
                }
                else{
                    System.out.println(("Syntax error: missing '\"' at line "+line_number + ", character "+character_count));
                    System.exit(-1);
                }
            }
            System.out.println(("Syntax error: Can not identitfy "+input[count]+
                    " at line "+line_number + ", character "+character_count));
            System.exit(-1);
        }
        return Tokens;
}//Hello
}