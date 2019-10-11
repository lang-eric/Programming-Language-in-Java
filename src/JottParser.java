
import javax.xml.soap.Node;
import java.util.List;
import java.util.Objects;

public class JottParser {
    private static int tokIndex;

    /* Values and their corresponding types as indicated in JottTokenizer.
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
     * A set of letters and numbers that begins with an uppercase letter            - upper_keyword
     * A set of letters and numbers that begins with a lower letter                 - lower_keyword
     * A set of letters, numbers, and spaces surrounded by quotation marks ('"')    - string
     *
     * NodeType constants, as indicated in ParseTreeNode:
     * PROGRAM, STMT_LIST, $$, STMT, END_STMT, EXPR, START_PAREN, END_PAREN,
     *	CHAR, L_CHAR, U_CHAR, DIGIT, SIGN, ID, PRINT, ASMT, OP,
     *	DBL, DBL_EXPR, INT, I_EXPR, STR_LITERAL, STR, STR_EXPR
     */

    public JottParser(){
        this.tokIndex = 0;
    }

    public static ParseTreeNode parseTokens(List<JottTokenizer.Token> tokenList) {
        ParseTreeNode root = new ParseTreeNode(null, NodeType.PROGRAM);
        ParseTreeNode stmtList = new ParseTreeNode(root, NodeType.STMT_LIST);
        expandStmtList(tokenList, stmtList);
        root.addChild(stmtList);
        root.addChild(new ParseTreeNode(root, NodeType.$$));



        return root;
    }

    private static String lookAhead(List<JottTokenizer.Token> tokenList, int tokIndex) {
        if (tokIndex < tokenList.size() - 1)
            return tokenList.get(tokIndex + 1).getType();
        else
            return null;
    }

    private static void expandStmtList(List<JottTokenizer.Token> tokenList, ParseTreeNode stmtList) {


            if (tokIndex == tokenList.size()) {
                stmtList.addChild(new ParseTreeNode(stmtList, NodeType.EPSILON));
            }
            else {
                ParseTreeNode stmt = new ParseTreeNode(stmtList, NodeType.STMT);
                ParseTreeNode stmtListNext = new ParseTreeNode(stmtList, NodeType.STMT_LIST);
                expandStmt(tokenList, stmt);
                expandStmtList(tokenList, stmtListNext);
                stmtList.addChild(stmt);
                stmtList.addChild(stmtListNext);
            }


    }

    private static void expandStmt(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        String type = tokenList.get(tokIndex).getType();
        if (type.equals("print")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.PRINT);
            stmt.addChild(prt);
        }

        else if (type.equals("type_Double")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.ASMT);
            expandASMT(tokenList, prt, NodeType.DOUBLE);
            stmt.addChild(prt);
        }

        else if (type.equals("type_Integer")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.ASMT);
            stmt.addChild(prt);
        }

        else if (type.equals("type_String")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.ASMT);
            stmt.addChild(prt);
        }

        else {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.EXPR);
            expandExpr(tokenList, prt);
            stmt.addChild(prt);
            tokIndex++;

            if (tokIndex != tokenList.size() - 1) {
                //TODO: error handling
            }

            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_STMT));
        }

    }

    private static void expandASMT(List<JottTokenizer.Token> tokenList, ParseTreeNode parent, NodeType type) {
        if (type.equals(NodeType.DOUBLE)) {
            ParseTreeNode child = new ParseTreeNode(parent, NodeType.DOUBLE);
            parent.addChild(child);
            tokIndex++;
            ParseTreeNode id = new ParseTreeNode(parent, NodeType.ID);
            expandId(tokenList, id);
            parent.addChild(id);

            tokIndex++;
            if (!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Missing assignment Error");
                return;
            }

            ParseTreeNode child2 = new ParseTreeNode(parent, "=");
            parent.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(parent, NodeType.D_EXPR);
            expandDExpr(tokenList, child3);
            parent.addChild(child3);

            parent.addChild(new ParseTreeNode(parent, NodeType.END_STMT));
            tokIndex ++;
        }
    }

    private static void expandExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode expr) {
        //TODO: implement <i_expr> case

        //TODO: implement <d_expr> case
        if (tokenList.get(tokIndex).getType().equals("type_Double")) {
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.D_EXPR);
            expandDExpr(tokenList, ptr);
        }

        //TODO: implement <s_expr> case

        //TODO: implement <id> case
    }

    private static void expandDExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode dexpr){
        String type = tokenList.get(tokIndex).getType();
        // <id> case
        if (type.equals("lower_keyword")) {
            ParseTreeNode node = new ParseTreeNode(dexpr, NodeType.ID);
            expandId(tokenList, node);
            dexpr.addChild(node);
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("double")) {
            ParseTreeNode node = new ParseTreeNode(dexpr, NodeType.DBL);
            expandDBL(tokenList, node);
            dexpr.addChild(node);
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("start_paren")) {
            ParseTreeNode child1 = new ParseTreeNode(dexpr, NodeType.D_EXPR);
            tokIndex ++;
            expandDExpr(tokenList, child1);
            dexpr.addChild(child1);

            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("end_paren")) {
            // TODO: handle error
//            if (tokenList.get(tokIndex + 1).getValue().equals("(") || tokenList.get(tokIndex + 1).getValue().equals("(")) {
//                tokIndex ++;
//            }
        }

        else if (type.equals("minus")) {

            if (!tokenList.get(tokIndex - 1).getValue().equals("+") && !tokenList.get(tokIndex - 1).getValue().equals("-") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("/") && !tokenList.get(tokIndex - 1).getValue().equals("*") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("^")) {
                ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP);
                ParseTreeNode leave = new ParseTreeNode(op, "-");
                op.addChild(leave);
                dexpr.addChild(op);
            }


            // TODO: check for error behind the plus sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("mult")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP);
            ParseTreeNode leave = new ParseTreeNode(op, "*");
            op.addChild(leave);
            dexpr.addChild(op);

            // TODO: check for error behind the plus sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("divide")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP);
            ParseTreeNode leave = new ParseTreeNode(op, "/");
            op.addChild(leave);
            dexpr.addChild(op);

            // TODO: check for error behind the sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("plus")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP);
            ParseTreeNode leave = new ParseTreeNode(op, "+");
            op.addChild(leave);
            dexpr.addChild(op);

            // TODO: check for error behind the sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("power")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP);
            ParseTreeNode leave = new ParseTreeNode(op, "^");
            op.addChild(leave);
            dexpr.addChild(op);
            // TODO: check for error behind the sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("end_stmt")) {
            //return
        }

        else {
            // TODO: Handle error
            System.out.println("Error read in");
        }



    }


    private static void expandId(List<JottTokenizer.Token> tokenList, ParseTreeNode id) {
        String val = tokenList.get(tokIndex).getValue();
        if (Character.isUpperCase(val.charAt(1))) {
            //error
            System.out.println("Id cannot begin with an uppercase letter");
            //return null;
        }
        ParseTreeNode ptr = new ParseTreeNode(id, NodeType.L_CHAR);
        ParseTreeNode leave = new ParseTreeNode(ptr, Character.toString(val.charAt(1)));
        ptr.addChild(leave);


        for (int i = 2; i < val.length(); i++) {
            if (val.charAt(i) == '[' | val.charAt(i) == ']' | val.charAt(i) == ',' | val.charAt(i) == ' '){
                continue;
            }

            ParseTreeNode child = new ParseTreeNode(ptr, NodeType.CHAR);
            expandChar(val.charAt(i), child);
            ptr.addChild(child);

        }
        id.addChild(ptr);
    }

    private static void expandChar(char c, ParseTreeNode parent){
        ParseTreeNode child = null;
        if (parent.getNodeType().equals(NodeType.L_CHAR))
            child = new ParseTreeNode(parent, NodeType.L_CHAR);
        else if (parent.getNodeType().equals(NodeType.U_CHAR))
            child = new ParseTreeNode(parent, NodeType.U_CHAR);
        else
            child = new ParseTreeNode(parent, NodeType.DIGIT);

        expandLUCharAndDigit(c, child);
        parent.addChild(child);

    }

    private static void expandLUCharAndDigit(char c, ParseTreeNode parent) {
        ParseTreeNode leave = new ParseTreeNode(parent, Character.toString(c));
        parent.addChild(leave);
    }


    private static void expandDBL(List<JottTokenizer.Token> tokenList, ParseTreeNode dbl) {
        String type = tokenList.get(tokIndex).getType();
        String val = tokenList.get(tokIndex).getValue();
        ParseTreeNode child = null;
        String sign = tokenList.get(tokIndex - 1).getType();

        if (sign.equals("minus")) {
            if (tokenList.get(tokIndex - 2).getType().equals("plus") || tokenList.get(tokIndex - 2).getType().equals("minus") || tokenList.get(tokIndex - 2).getType().equals("mult") || tokenList.get(tokIndex - 2).getType().equals("divide") || tokenList.get(tokIndex - 2).getType().equals("power")) {
                child = new ParseTreeNode(dbl, NodeType.MINUS);
            }
            else
                child = new ParseTreeNode(dbl, NodeType.PLUS);
        }

        else {
            child = new ParseTreeNode(dbl, NodeType.PLUS);
        }
        dbl.addChild(child);

        for (int i = 0; i < val.length(); i ++){
            if (val.charAt(i) == '[' | val.charAt(i) == ']' | val.charAt(i) == ',' | val.charAt(i) == ' '){
                continue;
            }
            expandLUCharAndDigit(val.charAt(i), dbl);
        }
    }



    private static boolean isFinalStatement(List<JottTokenizer.Token> tokenList, int tokIndex) {
        int i = tokIndex;
        while (!(tokenList.get(i).getType().equals("end_stmt")) && i < tokenList.size()) {
            i++;
        }
        return (i == tokenList.size() - 1 || i == tokenList.size());
    }


}
