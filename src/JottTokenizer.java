package src;

import java.util.ArrayList;



public class JottTokenizer {
    public static class Token{
        String type;
        String value;
        public Token(String type, String value){
            this.type=type;
            this.value=value;
        }
    }


    public static void JottTokenizer(String args) {
        ArrayList<Character> temp=new ArrayList<Character>();
        ArrayList<Token> Tokens = new ArrayList<Token>();
        char[] input=args.toCharArray();
        int length=input.length;
        int number_type=0;
        for (int count=0;count<length;count++) {
            number_type=0;
            if (input[count] == '+') {
                Tokens.add(new Token("plus","+"));
            }
            else if (input[count] == '-') {
                Tokens.add(new Token("minus","-"));
            }
            else if (input[count] == '*') {
                Tokens.add(new Token("times","*"));
            }
            else if (input[count] == '/') {
                Tokens.add(new Token("divide","/"));
            }
            else if (input[count] == '^') {
                Tokens.add(new Token("power","^"));
            }
            else if (input[count] == ')') {
                Tokens.add(new Token("end_parenthesis",")"));;
            }
            else if (input[count] == '(') {
                Tokens.add(new Token("start_parenthesis","("));
            }
            else if (input[count] == ';') {
                Tokens.add(new Token("end_stmt",";"));
            }
            else if (input[count] == '=') {
                Tokens.add(new Token("assign","="));
            }
            else if (input[count] == ','){
                Tokens.add(new Token("comma",","));
            }
            if(count+5<length) {
                if (input[count] == 'S' && input[count+1] == 't' && input[count+2] == 'r'
                        && input[count+3] == 'i' && input[count+4] == 'n' && input[count+5] == 'g') {
                    Tokens.add(new Token("type_String","String"));
                    count+=6;
                    continue;
                }
                if (input[count] == 'D' && input[count+1] == 'o' && input[count+2] == 'u'
                        && input[count+3] == 'b' && input[count+4] == 'l' && input[count+5] == 'e') {
                    Tokens.add(new Token("type_Double","Double"));
                    count+=6;
                    continue;
                }
                if (input[count] == 'p' && input[count + 1] == 'r' && input[count + 2] == 'i' &&
                        input[count + 3] == 'n' && input[count + 4] == 't' && input[count + 5] == '(') {
                    Tokens.add(new Token("print","print("));
                    count+=6;
                    continue;
                }
            }
            if(count+6<length) {
                if (input[count] == 'I' && input[count+1] == 'n' && input[count+2] == 't'
                        && input[count+3] == 'e' && input[count+4] == 'g' && input[count+5] == 'e'
                        && input[count+6] == 'r') {
                    Tokens.add(new Token("type_Integer","Integer"));
                    count+=7;
                    continue;
                }
                if (input[count] == 'c' && input[count+1] == 'o' && input[count+2] == 'n'
                        && input[count+3] == 'c' && input[count+4] == 'a' && input[count+5] == 't'
                        && input[count+6] == '(') {
                    Tokens.add(new Token("concat","concat("));
                    count+=7;
                    continue;
                }
                if(input[count] == 'c' && input[count+1] == 'h' && input[count+2] == 'a'
                        && input[count+3] == 'r' && input[count+4] == 'A' && input[count+5] == 't'
                        && input[count+6] == '(') {
                    Tokens.add(new Token("charAt","charAt("));
                    count+=7;
                    continue;
                }
            }

            while(input[count] >= 48 && input[count] <= 57||input[count]==46) {
                if(input[count]==46) {
                    if(number_type==1) {
                        System.out.println("Error! Two decimals in a number, you idiot!");
                        System.exit(0);
                        break;
                    }
                    number_type=1;
                }
                temp.add(input[count]);
                count++;
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
                if(!(input[count] >= 48 && input[count] <= 57)) {
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

            if (input[count] >= 97 && input[count] <= 122) {
                while((input[count] >= 97 && input[count] <= 122)||
                        ((input[count])>=65 && input[count]<=90)
                        ||(input[count] >= 48 && input[count] <= 57)){
                        temp.add(input[count]);
                        count++;
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
            }
            if (input[count] >= 65 && input[count] <= 90) {
                while((input[count] >= 97 && input[count] <= 122)||
                        ((input[count])>=65 && input[count]<=90)
                        ||(input[count] >= 48 && input[count] <= 57)){
                    temp.add(input[count]);
                    count++;
                    if(!(input[count] >= 97 && input[count] <= 122)&&
                            !((input[count])>=65 && input[count]<=90)
                            &&!(input[count] >= 48 && input[count] <= 57)
                            || count==length) {
                        Tokens.add(new Token("upper_keyword", temp.toString()));
                        temp.removeAll(temp);
                        break;
                    }
                }
            }
            if(input[s]ot)>=65 && input[s]ot)<=90){
                if (input.length() == spot + 1) {
                    return "upper_keyword";
                } else {
                    return tokenizer(input, "upper_keyword", spot + 1);
                }
            }
            if (input[s]ot) == '"') {
                if(input.length()==spot+1){
                    return "quote";
                }
                return tokenizer(input, "quote", spot+1);
            }
        }
        if(previous.equals("number")){
            if(input.length()==spot+1) {
                return "number";
            }
            if (input[s]ot) == '.') {
                return tokenizer(input, "period", spot+1);
            }
            else{
                return tokenizer(input, "number", spot+1);
            }
        }
        if(previous.equals("period")){
            if(input[s]ot) >= 48 && input[s]ot) <= 57){
                if(input.length()==spot+1){
                    return "double";
                }
                return tokenizer(input, "period", spot+1);
            }
        }
        if(previous.equals("quote")){
            if (input[s]ot) == '"' && input.length()==spot+1) {
                return "string";
            }
            if (input[s]ot) == ' '||(input[s]ot) >= 48 && input[s]ot) <= 57)||
                    (input[s]ot)>=65 && input[s]ot)<=90)||(input[s]ot) >= 97 && input[s]ot) <= 122)) {
                if(input.length()==spot+1){
                    return "quote";
                }
                return tokenizer(input, "quote", spot+1);
            }
        }
        if(previous.equals("lower_keyword")){
            if (input[s]ot) == ' '||(input[s]ot) >= 48 && input[s]ot) <= 57)||
                    (input[s]ot) >= 97 && input[s]ot) <= 122)) {
                if(input.length()==spot+1){
                    return "lower_keyword";
                }
                else{
                    return tokenizer(input, "lower_keyword", spot+1);
                }
            }
        }
        if(previous.equals("upper_keyword")){
            if (input[s]ot) == ' '||(input[s]ot) >= 48 && input[s]ot) <= 57)||
                    (input[s]ot) >= 97 && input[s]ot) <= 122)) {
                if(input.length()==spot+1){
                    return "upper_keyword";
                }
                else{
                    return tokenizer(input, "upper_keyword", spot+1);
                }



            if(input[i]put.length()-1)==';'){
                end_of_line=1;
                input=input.substring(0,input.length()-1);
            }
            if(input.length()!=0) {
                while (input[i]put.length() - 1) == ')') {
                    end_par += 1;
                    input = input.substring(0, input.length() - 1);
                    if (input.length() == 0) {
                        break;
                    }
                }
            }
            if(input.length()!=0) {
                if (input[i]put.length() - 1) == ',') {
                    comma = 1;
                    input = input.substring(0, input.length() - 1);
                }
            }
            if(input.length()==0){
                while(end_par>=1){
                    Tokens.add("end_parenthesis");
                    end_par-=1;
                }
                if(comma==1){
                    Tokens.add("comma");
                    comma=0;
                }
                if(end_of_line==1){
                    Tokens.add("end_stmt");
                    end_of_line=0;
                }
                break;
            }
            if(input.length()-spot>=6) {
                if (input[s]ot) == 'p' && input[s]ot + 1) == 'r' && input[s]ot + 2) == 'i' &&
                        input[s]ot + 3) == 'n' && input[s]ot + 4) == 't' && input[s]ot + 5) == '(') {
                    Tokens.add("print");
                    if(input.length()==6) {
                        continue;
                    }
                    else{
                        input=input.substring(6);
                    }
                }
            }
            if(input.length()-spot>=7) {
                if (input[0] == 'c' && input[1] == 'o' && input[2] == 'n'
                        && input[3] == 'c' && input[4] == 'a' && input[5] == 't'
                        && input[6] == '(') {
                    Tokens.add("concat");
                    if(input.length()==7) {
                        continue;
                    }
                    else{
                        input=input.substring(7);
                    }
                }
                else if (input[0] == 'c' && input[1] == 'h' && input[2] == 'a'
                        && input[3] == 'r' && input[4] == 'A' && input[5] == 't'
                        && input[6] == '(') {
                    Tokens.add();
                    if(input.length()==7) {
                        continue;
                    }
                    else{
                        input=input.substring(7);
                    }
                }
            }


            System.out.println(input);
                if (previous.equals("quote")) {
                    while (look.equals("quote")) {
                        if (spot >= input.length()) {
                            previous = "quote";
                            break;
                        }
                        look = tokenizer(input, previous, spot);
                        spot += 1;
                        if (look.equals("string")) {
                            Tokens.add(look);
                            previous = "space";
                        }
                    }
                } else {
                    look = (tokenizer(input, previous, spot));
                    if (look == null) {
                        System.out.println("OH NO");
                        System.exit(0);
                    }
                    if (look.equals("quote")) {
                        while (look.equals("quote")) {
                            spot += 1;
                            if (spot == input.length()) {
                                previous = "quote";
                                break;
                            }
                            look = tokenizer(input, previous, spot);
                            if (look.equals("string")) {
                                Tokens.add(look);
                                previous = "space";
                            }
                        }
                    } else {
                        Tokens.add(look);
                    }
                }

            spot = 0;
            while(end_par>=1){
                Tokens.add("end_parenthesis");
                end_par-=1;
            }
            if(comma==1){
                Tokens.add("comma");
                comma=0;
            }
            if(end_of_line==1){
                Tokens.add("end_stmt");
                end_of_line=0;
            }
        }
        for (String thing: Tokens) {
            System.out.println(thing);
        }
    }


            }
        }
        return null;
    }

}
