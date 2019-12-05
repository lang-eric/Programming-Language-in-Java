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
 * New to phase 2:
 * '<=' - less_eq
 * '<' - less
 * '>=' - greater_eq
 * '>' - greater
 * '==' - eq
 * '!=' - not_eq
 * 'if(' - if
 *  'else' - else
 *  'while(' - while
 *  'for(' - for
 * '{' - start_blk
 * '}' - end_blk
 *
 *  New to phase 3:
 * 'return' -return
 *
 * Author: Justin Kolodny
 */

import java.util.ArrayList;



public class JottTokenizer {
    private static VariableRegister variableRegister = new VariableRegister();

    public static VariableRegister getVariableRegister(){
        return variableRegister;
    }

    public static class Token{
        String type;
        String value;
        int character_count;
        int line;
        String line_string;
        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Token(String type, String value, int character_count, int line,String line_string){
            this.line_string=line_string;
            this.type=type;
            this.character_count=character_count;
            this.line=line;
            int count=0;
            if(value.length()==2&&value.charAt(0)=='['){
                this.value="";
            }
            else if(value.charAt(0)=='[') {
                StringBuilder builder = new StringBuilder(value.length());
                for (Character ch : value.toCharArray()) {
                    if (count % 3 == 1) {
                        builder.append(ch);
                    }
                    count++;
                }
                this.value = builder.toString();
            }
            else{
                this.value=value;
            }
        }

    }




    public static ArrayList<Token> JottTokenizer(String args, String filename){
        ArrayList<Character> temp=new ArrayList<>();
        ArrayList<Token> Tokens = new ArrayList<>();
        char[] input=args.toCharArray();
        int length=input.length;
        int number_type=0;
        int end=0;
        int line_number=1;
        int character_count=0;
        for (int count=0;count<length;count++) {
            if(length>count+2&&character_count==0){
                while(input[count]==' '){
                    count++;
                    if(count==length+1){
                        return Tokens;
                    }
                }
                //Catches commented line
                if(input[count]=='/'&&input[count+1]=='/'){
                    while(input[count]!='\n'){
                        count++;
                        if(count==length+1){
                            return Tokens;
                        }
                    }
                }
            }
            number_type=0;
            character_count++;
            //Catches newline
            if (input[count]=='\n'){
                line_number++;
                character_count=0;
                continue;
            }
            //Catches a whitespace
            if (input[count]== ' '){
                continue;
            }
            //Catches a 'start_blk'
            if(input [count]=='{'){
                Tokens.add(new Token("start_blk","{",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a 'end_blk'
            if(input [count]=='}'){
                Tokens.add(new Token("end_blk","}",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a plus
            if (input[count] == '+') {
                Tokens.add(new Token("plus","+",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a minus
            else if (input[count] == '-') {
                Tokens.add(new Token("minus","-",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a multiply
            else if (input[count] == '*') {
                Tokens.add(new Token("mult","*",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a divide
            else if (input[count] == '/') {
                Tokens.add(new Token("divide","/",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a power
            else if (input[count] == '^') {
                Tokens.add(new Token("power","^",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches an end parenthesis
            else if (input[count] == ')') {
                Tokens.add(new Token("end_paren",")",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches an start parenthesis
            else if (input[count] == '(') {
                Tokens.add(new Token("start_paren","(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches an end of a statement
            else if (input[count] == ';') {
                Tokens.add(new Token("end_stmt",";",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            else if (input[count]=='!'){
                count++;
                character_count++;
                if (count == length) {
                    System.out.println("Syntax error: '!' is not a valid operator, you must make it " +
                            "'!='. At " + line_number + ", character " + (character_count + 1) +
                            "\n\"" + JottRunner.line_list.get(line_number - 1) + "\"");
                    System.exit(-1);
                }
                else if (input[count] == '='){
                    Tokens.add(new Token("not_eq", "!=", character_count, line_number, JottRunner.line_list.get(line_number - 1)));
                    continue;
                }
                else{
                    System.out.println("Syntax error: '!' is not a valid operator, you must make it " +
                            "'!='. At " + line_number + ", character " + (character_count + 1) +
                            "\n\"" + JottRunner.line_list.get(line_number - 1) + "\"");
                    System.exit(-1);
                }
            }

            //Catches an assign
            else if (input[count] == '=') {
                if (count+1 == length) {
                    return Tokens;
                }
                else if(input[count+1]=='='){
                    Tokens.add(new Token("eq","==",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count++;
                    character_count++;
                    continue;
                }
                else {
                    Tokens.add(new Token("assign", "=", character_count, line_number, JottRunner.line_list.get(line_number - 1)));
                    continue;
                }
            }
            //Catches a comma
            else if (input[count] == ',') {
                Tokens.add(new Token("comma", ",",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                continue;
            }
            //Catches a <= and <
            else if (input[count] == '<') {
                if (input[count+1] == '='){
                    count++;
                    character_count++;
                    Tokens.add(new Token("less_eq", "<=", character_count, line_number, JottRunner.line_list.get(line_number - 1)));
                    continue;
                }
                else{
                    Tokens.add(new Token("less","<",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    continue;
                }
            }
            //Catches a >= and >
            else if (input[count] == '>') {
                if (input[count+1] == '='){
                    count++;
                    character_count++;
                    Tokens.add(new Token("greater_eq", ">=", character_count, line_number, JottRunner.line_list.get(line_number - 1)));
                    continue;
                }
                else{
                    Tokens.add(new Token("greater",">",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    continue;
                }
            }
            String inputString = new String(input); //convert the char[] into a string, for readable comparison's sake.
            if(count+2<length){
                if(inputString.substring(count,count+3).equals("if(")){
                    Tokens.add(new Token("if","if(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=2;
                    character_count+=2;
                    continue;
                }
            }
            if(count+3<length){
                if(inputString.substring(count,count+4).equals("else")){
                    Tokens.add(new Token("else","else",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=3;
                    character_count+=3;
                    continue;
                }
                if(inputString.substring(count,count+4).equals("for(")){
                    Tokens.add(new Token("for","for(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=3;
                    character_count+=3;
                    continue;
                }
            }
            if(count+5<length) {
                if (inputString.substring(count, count+6).equals("String")){
                    Tokens.add(new Token("type_String","String",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=5;
                    character_count+=5;
                    continue;
                }
                else if (inputString.substring(count, count+6).equals("Double")){
                    Tokens.add(new Token("type_Double","Double",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=5;
                    character_count+=5;
                    continue;
                }
                else if (inputString.substring(count, count+6).equals("print(")) {
                    Tokens.add(new Token("print","print(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=5;
                    character_count+=5;
                    continue;
                }
                else if(inputString.substring(count,count+6).equals("while(")){
                    Tokens.add(new Token("while","while(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=5;
                    character_count+=5;
                    continue;
                }
            }
            if(count+6<length) {
                if (inputString.substring(count, count+7).equals("Integer")) {
                    Tokens.add(new Token("type_Integer","Integer",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                else if (inputString.substring(count,count+6).equals("return")){
                    Tokens.add(new Token("return","return",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=6;
                    character_count+=6;
                    continue;
                }
            }

            if(count+7<length) {
                //Catches a concat statement
                if (inputString.substring(count, count+7).equals("concat(")) {
                    Tokens.add(new Token("concat","concat(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=6;
                    character_count+=6;
                    continue;
                }
                //Catches a charAt statement
                else if (inputString.substring(count, count+7).equals("charAt(")) {
                    Tokens.add(new Token("charAt","charAt(",character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    count+=6;
                    character_count+=6;
                    continue;
                }
            }

            while(Character.isDigit(input[count]) || input[count]=='.') {
                end=1;
                if(input[count]=='.') {
                    if(number_type==1) {
                        System.out.println("Syntax error: Two '.'s in a row " +
                                "does not make a number at "+line_number + ", character "+character_count+
                        "\n\""+JottRunner.line_list.get(line_number-1)+"\"");
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
                        character_count=0;
                        if(number_type==1) {
                            Tokens.add(new Token("double",temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        }
                        else {
                            Tokens.add(new Token("integer", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        }
                        line_number++;
                        break;
                    }
                }
                if(count==length) {
                    if(number_type==1) {
                        Tokens.add(new Token("double",temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    }
                    else {
                        Tokens.add(new Token("integer", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    }
                    temp.removeAll(temp);
                    break;
                }
                if(!(Character.isDigit(input[count])) && !(input[count]=='.')) {

                    if(number_type==1) {
                        Tokens.add(new Token("double",temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    }
                    else {
                        Tokens.add(new Token("integer", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    }
                    temp.removeAll(temp);
                    count--;
                    break;
                }
            }
            if(end==1){
                end=0;
                continue;
            }
            //Catches lowercase keywords
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
                            Tokens.add(new Token("lower_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                            temp.removeAll(temp);
                            break;
                        }
                    }
                    if(count==length) {
                        Tokens.add(new Token("lower_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        temp.removeAll(temp);
                        count--;
                        break;
                    }
                    if(!(Character.isLowerCase(input[count]))&&
                            !((Character.isUpperCase(input[count]))
                                    &&!(Character.isDigit(input[count])))){
                        Tokens.add(new Token("lower_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        temp.removeAll(temp);
                        count--;
                        break;
                    }
                }
                continue;
            }
            //Catches uppercase keywords
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
                            Tokens.add(new Token("upper_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                            temp.removeAll(temp);
                            break;
                        }
                    }
                    if(count==length) {
                        Tokens.add(new Token("upper_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        temp.removeAll(temp);
                        count--;
                        break;
                    }
                    if(!(Character.isLowerCase(input[count])&&
                            !(Character.isUpperCase(input[count]))
                            &&!(Character.isDigit(input[count])))){
                        Tokens.add(new Token("upper_keyword", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                        temp.removeAll(temp);
                        count--;
                        break;
                    }
                }
                continue;
            }
            //Catches strings
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
                        System.out.println("Syntax Error: Missing " + "\"" + ", " + "\"" + JottRunner.line_list.get(line_number-1) + "\"" + " (" + filename + ":" + line_number + ")");
                        System.exit(-1);
                    }
                }
                if(input[count]=='"'){
                    Tokens.add(new Token("string", temp.toString(),character_count,line_number, JottRunner.line_list.get(line_number-1)));
                    temp.removeAll(temp);
                    end=1;
                    break;
                }
                else{
                    System.out.println("Syntax Error: Missing " + "\"" + ", " + "\"" + JottRunner.line_list.get(line_number-1) + "\"" + " (" + filename + ":" + line_number + ")");
                    System.exit(-1);
                }
            }
            if(end==1){
                end=0;
                continue;
            }
            System.out.println("Syntax error: Can not identify "+input[count]+
                    " at line "+line_number + ", character "+character_count+
                    "\n\""+JottRunner.line_list.get(line_number-1)+"\"");
            System.exit(-1);
        }
//        for(int i = 0; i< Tokens.size(); i++){
//            if(Tokens.get(i).type.equals("assign"))
//            {
//                System.out.println(Tokens.get(i+1).getType());
//                variableRegister.addVariable(Tokens.get(i-1).getValue(), Tokens.get(i+1).type, Tokens.get(i+1).value);
//            }
//
//        }
        return Tokens;

    }

}