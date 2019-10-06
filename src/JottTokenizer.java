package src;

import java.util.ArrayList;

public class JottTokenizer {
    public static void main(String[] args) {
        ArrayList<String> Tokens = new ArrayList<String>();
        String look="space";
        int spot=0;
        int end_of_line=0;
        int end_par=0;
        int comma=0;
        String previous="space";
        for (String token:args) {
            if(token.charAt(token.length()-1)==';'){
                end_of_line=1;
                token=token.substring(0,token.length()-1);
            }
            if (token.charAt(token.length() - 1) == ')') {
                end_par = 1;
                token = token.substring(0, token.length() - 1);
            }
            if(token.charAt(token.length()-1)==','){
                comma=1;
                token=token.substring(0,token.length()-1);
            }
            if(token.length()-spot>=6) {
                if (token.charAt(spot) == 'p' && token.charAt(spot + 1) == 'r' && token.charAt(spot + 2) == 'i' &&
                        token.charAt(spot + 3) == 'n' && token.charAt(spot + 4) == 't' && token.charAt(spot + 5) == '(') {
                    Tokens.add("print");
                    if(token.length()==6) {
                        continue;
                    }
                    else{
                        token=token.substring(6);
                    }
                }
            }
            if(token.length()-spot>=7) {
                if (token.charAt(0) == 'c' && token.charAt(1) == 'o' && token.charAt(2) == 'n'
                        && token.charAt(3) == 'c' && token.charAt(4) == 'a' && token.charAt(5) == 't'
                        && token.charAt(6) == '(') {
                    Tokens.add("concat");
                    if(token.length()==7) {
                        continue;
                    }
                    else{
                        token=token.substring(7);
                    }
                }
                else if (token.charAt(0) == 'c' && token.charAt(1) == 'h' && token.charAt(2) == 'a'
                        && token.charAt(3) == 'r' && token.charAt(4) == 'A' && token.charAt(5) == 't'
                        && token.charAt(6) == '(') {
                    Tokens.add("charAt");
                    if(token.length()==7) {
                        continue;
                    }
                    else{
                        token=token.substring(7);
                    }
                }
            }


            System.out.println(token);
            if (previous.equals("quote")) {
                while (look.equals("quote")) {
                    if (spot >= token.length()) {
                        previous = "quote";
                        break;
                    }
                    look = tokenizer(token, previous, spot);
                    spot += 1;
                    if (look.equals("string")) {
                        Tokens.add(look);
                        previous = "space";
                    }
                }
            }
            else {
                look = (tokenizer(token, previous, spot));
                if (look == null) {
                    System.out.println("OH NO");
                    System.exit(0);
                }
                if (look.equals("quote")) {
                    previous = "quote";
                    look = tokenizer(token, previous, spot);
                    spot += 1;
                    if (look == null) {
                        System.out.println("OH NO");
                        System.exit(0);
                    }
                    while (look.equals("quote")) {
                        spot += 1;
                        if (spot == token.length()) {
                            previous = "quote";
                            break;
                        }
                        look = tokenizer(token, previous, spot);
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
            if(end_par==1){
                Tokens.add("end_parenthesis");
                end_par=0;
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

    private static String tokenizer(String token, String previous, int spot) {
        if(previous.equals("space")) {
            if(token.length()==6) {
                if (token.charAt(0) == 'S' && token.charAt(1) == 't' && token.charAt(2) == 'r'
                        && token.charAt(3) == 'i' && token.charAt(4) == 'n' && token.charAt(5) == 'g') {
                    return "type_String";
                }
                if (token.charAt(0) == 'D' && token.charAt(1) == 'o' && token.charAt(2) == 'u'
                        && token.charAt(3) == 'b' && token.charAt(4) == 'l' && token.charAt(5) == 'e') {
                    return "type_Double";
                }
                if (token.charAt(spot) == 'p' && token.charAt(spot + 1) == 'r' && token.charAt(spot + 2) == 'i' &&
                        token.charAt(spot + 3) == 'n' && token.charAt(spot + 4) == 't' && token.charAt(spot + 5) == '(') {
                    return "print";
                }
            }
            if(token.length()==7) {
                if (token.charAt(0) == 'I' && token.charAt(1) == 'n' && token.charAt(2) == 't'
                        && token.charAt(3) == 'e' && token.charAt(4) == 'g' && token.charAt(5) == 'e'
                        && token.charAt(4) == 'r') {
                    return "type_Integer";
                }
                if (token.charAt(0) == 'c' && token.charAt(1) == 'o' && token.charAt(2) == 'n'
                        && token.charAt(3) == 'c' && token.charAt(4) == 'a' && token.charAt(5) == 't'
                        && token.charAt(6) == '(') {
                    return "concat";
                }
                if(token.charAt(0) == 'c' && token.charAt(1) == 'h' && token.charAt(2) == 'a'
                        && token.charAt(3) == 'r' && token.charAt(4) == 'A' && token.charAt(5) == 't'
                        && token.charAt(6) == '(') {
                    return "charAt";
                }
            }
            if (token.charAt(spot) == '+' && token.length()==1) {
                return "plus";
            }
            if (token.charAt(spot) == '-' && token.length()==1) {
                return "minus";
            }
            if (token.charAt(spot) == '*' && token.length()==1) {
                return "mult";
            }
            if (token.charAt(spot) == '/' && token.length()==1) {
                return "divide";
            }
            if (token.charAt(spot) == '^'&& token.length()==1) {
                return "power";
            }
            if (token.charAt(spot) == ')' && token.length()==1) {
                return "end_parenthesis";
            }
            if (token.charAt(spot) == '(' && token.length()==1) {
                return "start_parenthesis";
            }
            if (token.charAt(spot) == ';' && token.length()==1) {
                return "end_stmt";
            }
            if (token.charAt(spot) == '=' && token.length()==1) {
                return "assign";
            }
            if (token.charAt(spot) == ',' && token.length()==1){
                return "comma";
            }
            if (token.charAt(spot) >= 48 && token.charAt(spot) <= 57) {
                if(token.length()==spot+1) {
                    return "number";
                }
                else{
                    return tokenizer(token, "number", spot+1);
                }
            }
            if (token.charAt(spot) == '.') {
                return tokenizer(token, "period", spot+1);
            }
            if ((token.charAt(spot) >= 97 && token.charAt(spot) <= 122)||(token.charAt(spot)>=65 && token.charAt(spot)<=90)) {
                if (token.length() == spot + 1) {
                    return "keyword";
                } else {
                    return tokenizer(token, "keyword", spot + 1);
                }
            }
            if (token.charAt(spot) == '"') {
                if(token.length()==spot+1){
                    return "quote";
                }
                return tokenizer(token, "quote", spot+1);
            }
        }
        if(previous.equals("number")){
            if(token.length()==spot+1) {
                return "number";
            }
            if (token.charAt(spot) == '.') {
                return tokenizer(token, "period", spot+1);
            }
            else{
                return tokenizer(token, "number", spot+1);
            }
        }
        if(previous.equals("period")){
            if(token.charAt(spot) >= 48 && token.charAt(spot) <= 57){
                if(token.length()==spot+1){
                    return "double";
                }
                return tokenizer(token, "period", spot+1);
            }
        }
        if(previous.equals("quote")){
            if (token.charAt(spot) == '"' && token.length()==spot+1) {
                return "string";
            }
            if (token.charAt(spot) == ' '||(token.charAt(spot) >= 48 && token.charAt(spot) <= 57)||
                    (token.charAt(spot)>=65 && token.charAt(spot)<=90)||(token.charAt(spot) >= 97 && token.charAt(spot) <= 122)) {
                if(token.length()==spot+1){
                    return "quote";
                }
                return tokenizer(token, "quote", spot+1);
            }
        }
        if(previous.equals("keyword")){
            if (token.charAt(spot) == ' '||(token.charAt(spot) >= 48 && token.charAt(spot) <= 57)||
                    (token.charAt(spot)>=65 && token.charAt(spot)<=90)||(token.charAt(spot) >= 97 && token.charAt(spot) <= 122)) {
                if(token.length()==spot+1){
                    return "keyword";
                }
                else{
                    return tokenizer(token, "keyword", spot+1);
                }
            }
        }
        return null;
    }

}
