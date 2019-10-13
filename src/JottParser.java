import java.util.HashMap;
import java.util.List;

public class JottParser {


    private static int tokIndex = 0;
    private static String cur_varName = "";
    private static NodeType cur_type = null;
    private static HashMap<String, NodeType> map = new HashMap<>();

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


//        traverseTree(root, 0);
        return root;
    }

    private static String lookAhead(List<JottTokenizer.Token> tokenList, int tokIndex) {
        if (tokIndex < tokenList.size() - 1)
            return tokenList.get(tokIndex + 1).getType();
        else
            return null;
    }

    private static void expandStmtList(List<JottTokenizer.Token> tokenList, ParseTreeNode stmtList) {
            if (tokIndex >= tokenList.size()) {
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
            expandPrint(tokenList, prt);
            stmt.addChild(prt);
        }

        else if (type.equals(NodeType.END_STMT)) {
            tokIndex ++;
            if (tokIndex < tokenList.size()) {
                expandStmt(tokenList, stmt);
            }
        }

        else if (type.equals("type_Double")) {
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);
            expandASMT(tokenList, asmt, NodeType.DOUBLE);
            stmt.addChild(asmt);
        }

        else if (type.equals("type_Integer")) {
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);
            expandASMT(tokenList, asmt, NodeType.INTEGER);
            stmt.addChild(asmt);
        }

        else if (type.equals("type_String")) {
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);
            expandASMT(tokenList, asmt, NodeType.STR);
            stmt.addChild(asmt);
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
            cur_type = NodeType.DOUBLE;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            parent.addChild(id);

            tokIndex++;
            if (!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Missing assignment Error");
                return;
            }

            ParseTreeNode child2 = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(parent, NodeType.D_EXPR);
            expandDExpr(tokenList, child3);
            parent.addChild(child3);

            parent.addChild(new ParseTreeNode(parent, NodeType.END_STMT));
            tokIndex ++;
        }

        else if (type.equals(NodeType.INTEGER)) {
            ParseTreeNode child = new ParseTreeNode(parent, NodeType.INTEGER);
            parent.addChild(child);
            tokIndex++;
            ParseTreeNode id = new ParseTreeNode(parent, NodeType.ID);
            expandId(tokenList, id);
            cur_type = NodeType.INTEGER;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            parent.addChild(id);

            tokIndex++;
            if (!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Missing assignment Error");
                return;
            }

            ParseTreeNode child2 = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(parent, NodeType.I_EXPR);
            expandIExpr(tokenList, child3);
            parent.addChild(child3);

            parent.addChild(new ParseTreeNode(parent, NodeType.END_STMT));
            tokIndex ++;
        }

        else if (type.equals(NodeType.STR)) {
            ParseTreeNode str = new ParseTreeNode(parent, NodeType.STRING, "String");
            parent.addChild(str);
            tokIndex += 1;
            ParseTreeNode id = new ParseTreeNode(parent, NodeType.ID);
            expandId(tokenList, id);
            cur_type = NodeType.STR;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            parent.addChild(id);
            tokIndex += 1;

            if(!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Error: assignment expected, " + tokenList.get(tokIndex).getValue() + " found.");
                return;
            }
            ParseTreeNode assignment = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(assignment);
            tokIndex += 1;

            ParseTreeNode s_expr = new ParseTreeNode(parent, NodeType.S_EXPR);
            expandSExpr(tokenList, s_expr);
            parent.addChild(s_expr);

            parent.addChild(new ParseTreeNode(parent, NodeType.END_STMT));
            tokIndex += 1;
        }
    }

    private static void expandExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode expr) {
        //TODO: implement <i_expr> case
        if (tokenList.get(tokIndex).getType().equals("integer")){
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.I_EXPR);
            expandIExpr(tokenList, ptr);
            expr.addChild(ptr);
        }

        //TODO: implement <d_expr> case
        else if (tokenList.get(tokIndex).getType().equals("double")) {
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.D_EXPR);
            expandDExpr(tokenList, ptr);
            expr.addChild(ptr);
        }

        //TODO: implement <s_expr> case
        else if (tokenList.get(tokIndex).getType().equals("string") || tokenList.get(tokIndex).getType().equals("concat")
                || tokenList.get(tokIndex).getType().equals("charAt")) {
            ParseTreeNode s_expr = new ParseTreeNode(expr, NodeType.S_EXPR);
            expandSExpr(tokenList, s_expr);
            expr.addChild(s_expr);
        }

        //TODO: implement <id> case
        else if (tokenList.get(tokIndex).getType().equals("lower_keyword")) {
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.ID);

            if (tokenList.get(tokIndex + 1).getType().equals("plus") || tokenList.get(tokIndex + 1).getType().equals("mult") ||
                    tokenList.get(tokIndex + 1).getType().equals("minus") || tokenList.get(tokIndex + 1).getType().equals("divide") ||
                    tokenList.get(tokIndex + 1).getType().equals("power")){
                NodeType type = map.get(tokenList.get(tokIndex).getValue());
                if (type.equals(NodeType.DOUBLE)) {
                    ptr = new ParseTreeNode(expr, NodeType.D_EXPR);
                    expandDExpr(tokenList, ptr);
                    expr.addChild(ptr);
                }

                else if (type.equals(NodeType.INTEGER)) {
                    ptr = new ParseTreeNode(expr, NodeType.I_EXPR);
                    expandIExpr(tokenList, ptr);
                    expr.addChild(ptr);
                }

                //check
                else if (type.equals(NodeType.STR)) {
                    ptr = new ParseTreeNode(expr, NodeType.S_EXPR);
                    expandSExpr(tokenList, ptr);
                    expr.addChild(ptr);
                }

                else {
                    //error handling
                    System.out.println("No variable found...");
                }
            }

            else {
                NodeType type = map.get(tokenList.get(tokIndex).getValue());
                if (type == null) {
                    //TODO: ERROR
                }
                ptr = new ParseTreeNode(expr, NodeType.ID);
                expandId(tokenList, ptr);
                expr.addChild(ptr);
            }
            //expandId(tokenList, ptr);
            //expr.addChild(ptr);
        }

        else if (tokenList.get(tokIndex).getType().equals("minus")) {
            if (tokIndex + 1  == tokenList.size()) {
                //TODO:ERROR
            }

            else if (tokenList.get(tokIndex + 1).getType().equals("double")) {
                ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.D_EXPR);
                expandDExpr(tokenList, ptr);
                expr.addChild(ptr);
            }

            else if (tokenList.get(tokIndex + 1).getType().equals("integer")){
                ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.I_EXPR);
                expandIExpr(tokenList, ptr);
                expr.addChild(ptr);
            }
        }




        else {
            //TODO: ERROR
            /*JottTokenizer.Token tikToken = tokenList.get(tokIndex);
            System.out.println("Error encountered in parsing expression at "
                    + tikToken.line + ":" + tikToken.character_count + " " + tikToken.getValue());
            traverseTree(expr.getRootNode(), 0);
            traverseTree(expr, 1);
            System.exit(1);*/
        }
    }


    private static void expandIExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode dexpr) {
        String type = tokenList.get(tokIndex).getType();
        List<ParseTreeNode> children = dexpr.getAllChildren();


        // <id> case
        if (type.equals("lower_keyword")) {
            ParseTreeNode node = new ParseTreeNode(dexpr, NodeType.ID);
            expandId(tokenList, node);
            dexpr.addChild(node);
            tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if (type.equals("integer")) {
            ParseTreeNode node = new ParseTreeNode(dexpr, NodeType.INT);
            expandDBL(tokenList, node);
            dexpr.addChild(node);
            tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if (type.equals("start_paren")) {
            ParseTreeNode child1 = new ParseTreeNode(dexpr, NodeType.I_EXPR);
            tokIndex ++;
            expandIExpr(tokenList, child1);
            dexpr.addChild(child1);

            tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if (type.equals("end_paren")) {
            // TODO: handle error
        }

        else if (type.equals("minus")) {

            if (!tokenList.get(tokIndex - 1).getValue().equals("+") && !tokenList.get(tokIndex - 1).getValue().equals("-") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("/") && !tokenList.get(tokIndex - 1).getValue().equals("*") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("^") && !tokenList.get(tokIndex - 1).getValue().equals("(") &&
                    !tokenList.get(tokIndex - 1).getType().equals("print")) {
                ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "-");


                if (children.size() > 1) {
                    if (children.size() == 3) {
                        ParseTreeNode child = new ParseTreeNode(dexpr, NodeType.I_EXPR);
                        children.get(0).setParent(child);
                        children.get(1).setParent(child);
                        children.get(2).setParent(child);
                        child.addChild(children.get(0));
                        child.addChild(children.get(1));
                        child.addChild(children.get(2));
                        dexpr.removeAllChild();

                        dexpr.addChild(child);
                        dexpr.addChild(op);

                        tokIndex ++;
                        expandIExpr(tokenList, dexpr);
                    }
                    else {
                        //TODO:ERROR
                    }
                }


                else {
                    dexpr.addChild(op);
                }
            }



            // TODO: check for error behind the plus sign
            tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if (type.equals("plus") || type.equals("divide") || type.equals("power") || type.equals("mult")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "+");
            if (type.equals("divide")) op = new ParseTreeNode(dexpr, NodeType.OP, "/");
            if (type.equals("mult")) op = new ParseTreeNode(dexpr, NodeType.OP, "*");
            if (type.equals("power")) op = new ParseTreeNode(dexpr, NodeType.OP, "^");


            if (children.size() > 1) {
                if (children.size() == 3) {
                    ParseTreeNode child = new ParseTreeNode(dexpr, NodeType.I_EXPR);
                    children.get(0).setParent(child);
                    children.get(1).setParent(child);
                    children.get(2).setParent(child);
                    child.addChild(children.get(0));
                    child.addChild(children.get(1));
                    child.addChild(children.get(2));
                    dexpr.removeAllChild();

                    dexpr.addChild(child);
                    dexpr.addChild(op);

                    tokIndex ++;
                    expandIExpr(tokenList, dexpr);
                }
                else {
                    //TODO:ERROR
                }
            }

            else {
                dexpr.addChild(op);
            }

            // TODO: check for error behind the sign
            tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }


        else if (type.equals("end_stmt")) {
            //return
        }

        else {
            // TODO: Handle error
            System.out.println("Error read in");
        }

    }

    private static void expandSExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode s_expr) {
        String sExprType = tokenList.get(tokIndex).getType();
        switch (sExprType) {
            case "string":
                ParseTreeNode str_literal = new ParseTreeNode(s_expr, NodeType.STR_LITERAL);
                expandStrLiteral(tokenList, str_literal);
                s_expr.addChild(str_literal);
                tokIndex += 1;
                break;
            case "lower_keyword":
                ParseTreeNode s_id = new ParseTreeNode(s_expr, NodeType.ID);
                expandId(tokenList, s_id);
                s_expr.addChild(s_id);
                tokIndex += 1;
                break;
            case "concat":
                ParseTreeNode cat = new ParseTreeNode(s_expr, NodeType.CONCAT,"concat");
                s_expr.addChild(cat);
                addStartParen(s_expr);
                tokIndex += 1;
                ParseTreeNode s1 = new ParseTreeNode(s_expr, NodeType.S_EXPR);
                expandSExpr(tokenList, s1);
                s_expr.addChild(s1);
                if (!tokenList.get(tokIndex).getType().equals("comma")) {
                    //TODO: generate error: comma expected
                    break;
                }
                s_expr.addChild(new ParseTreeNode(s_expr, NodeType.COMMA, ","));
                tokIndex += 1;
                ParseTreeNode s2 = new ParseTreeNode(s_expr, NodeType.S_EXPR);
                expandSExpr(tokenList, s2);
                s_expr.addChild(s2);
                addEndParen(s_expr);
                tokIndex += 1;
                break;
            case "charAt":
                ParseTreeNode charAt = new ParseTreeNode(s_expr, NodeType.CHARAT, "charAt");
                s_expr.addChild(charAt);
                addStartParen(s_expr);
                tokIndex += 1;
                ParseTreeNode targetS = new ParseTreeNode(s_expr, NodeType.S_EXPR);
                expandSExpr(tokenList, targetS);
                s_expr.addChild(targetS);
                if (!tokenList.get(tokIndex).getType().equals("comma")) {
                    //TODO: generate error: comma expected
                    break;
                }
                s_expr.addChild(new ParseTreeNode(s_expr, NodeType.COMMA, ","));
                tokIndex += 1;
                ParseTreeNode i_expr = new ParseTreeNode(s_expr, NodeType.I_EXPR);
                expandIExpr(tokenList, i_expr);
                s_expr.addChild(i_expr);
                addEndParen(s_expr);
                tokIndex += 1;
                break;
        }
    }

    /**
     * Adds in a start_paren node into the parent's children.
     * @param parent a start_paren node will be added to this node's children list
     */
    private static void addStartParen(ParseTreeNode parent) {
        ParseTreeNode start_paren = new ParseTreeNode(parent, NodeType.START_PAREN);
        start_paren.addChild(new ParseTreeNode(start_paren, NodeType.START_PAREN, "("));
        parent.addChild(start_paren);
    }

    /**
     * Adds in an end_paren node into the parent's children.
     * @param parent an end_paren node will be added to this node's children list
     */
    private static void addEndParen(ParseTreeNode parent) {
        ParseTreeNode end_paren = new ParseTreeNode(parent, NodeType.END_PAREN);
        end_paren.addChild(new ParseTreeNode(end_paren, NodeType.END_PAREN, ")"));
        parent.addChild(end_paren);
    }

    private static void expandStrLiteral(List<JottTokenizer.Token> tokenList, ParseTreeNode str_literal) {
        str_literal.addChild(new ParseTreeNode(str_literal, NodeType.QUOTE,"\""));
        ParseTreeNode str = new ParseTreeNode(str_literal, NodeType.STR);
        str_literal.addChild(str);
        str.addChild(new ParseTreeNode(str, NodeType.STR, tokenList.get(tokIndex).getValue()));
        str_literal.addChild(new ParseTreeNode(str_literal, NodeType.QUOTE, "\""));
    }

    private static void expandDExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode dexpr){
        String type = tokenList.get(tokIndex).getType();
        List<ParseTreeNode> children = dexpr.getAllChildren();

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
        }

        else if (type.equals("minus")) {

            if (!tokenList.get(tokIndex - 1).getValue().equals("+") && !tokenList.get(tokIndex - 1).getValue().equals("-") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("/") && !tokenList.get(tokIndex - 1).getValue().equals("*") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("^") && !tokenList.get(tokIndex - 1).getValue().equals("(") &&
                    !tokenList.get(tokIndex - 1).getType().equals("print")) {
                ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "-");


                if (children.size() > 1) {
                    if (children.size() == 3) {
                        ParseTreeNode child = new ParseTreeNode(dexpr, NodeType.D_EXPR);
                        children.get(0).setParent(child);
                        children.get(1).setParent(child);
                        children.get(2).setParent(child);
                        child.addChild(children.get(0));
                        child.addChild(children.get(1));
                        child.addChild(children.get(2));
                        dexpr.removeAllChild();

                        dexpr.addChild(child);
                        dexpr.addChild(op);

                        tokIndex ++;
                        expandDExpr(tokenList, dexpr);
                    }
                    else {
                        //TODO:ERROR
                    }
                }


                else {
                    dexpr.addChild(op);
                }
            }



            // TODO: check for error behind the plus sign
            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("plus") || type.equals("divide")  || type.equals("power") || type.equals("mult")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "+");
            if (type.equals("divide")) op = new ParseTreeNode(dexpr, NodeType.OP, "/");
            if (type.equals("mult")) op = new ParseTreeNode(dexpr, NodeType.OP, "*");
            if (type.equals("power")) op = new ParseTreeNode(dexpr, NodeType.OP, "^");


            if (children.size() > 1) {
                if (children.size() == 3) {
                    ParseTreeNode child = new ParseTreeNode(dexpr, NodeType.D_EXPR);
                    children.get(0).setParent(child);
                    children.get(1).setParent(child);
                    children.get(2).setParent(child);
                    child.addChild(children.get(0));
                    child.addChild(children.get(1));
                    child.addChild(children.get(2));
                    dexpr.removeAllChild();

                    dexpr.addChild(child);
                    dexpr.addChild(op);

                    tokIndex ++;
                    expandDExpr(tokenList, dexpr);
                }
                else {
                    //TODO:ERROR
                }
            }

            else {
                dexpr.addChild(op);
            }

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
        String res = "";
        if (Character.isUpperCase(val.charAt(0))) {
            //error
            System.out.println("Id cannot begin with an uppercase letter");
        }
        res += val.charAt(0);


        for (int i = 1; i < val.length(); i++) {
            //TODO: check for error
        }

        id.setValue(val);
    }


    private static void expandDBL(List<JottTokenizer.Token> tokenList, ParseTreeNode dbl) {
        //String type = tokenList.get(tokIndex).getType();
        String val = tokenList.get(tokIndex).getValue();
        ParseTreeNode child = null;
        String sign = tokenList.get(tokIndex - 1).getType();
        String newSign = "";

        if (sign.equals("minus")) {
            if (tokenList.get(tokIndex - 2).getType().equals("plus") || tokenList.get(tokIndex - 2).getType().equals("minus") ||
                    tokenList.get(tokIndex - 2).getType().equals("mult") || tokenList.get(tokIndex - 2).getType().equals("divide") ||
                    tokenList.get(tokIndex - 2).getType().equals("power") || tokenList.get(tokIndex - 2).getType().equals("start_paren") ||
                    tokenList.get(tokIndex - 2).getType().equals("print")) {
                newSign = "-";
            }
            else {
                newSign = "+";
            }
        }

        else {
            newSign = "+";
        }

        if (newSign.equals("+")) {
            dbl.setValue(val);
        }

        else {
            dbl.setValue("-" + val);
        }
    }



    public static void expandPrint(List<JottTokenizer.Token> tokenList, ParseTreeNode print) {
        ParseTreeNode child1 = new ParseTreeNode(print, NodeType.START_PAREN);
        ParseTreeNode child2 = new ParseTreeNode(print, NodeType.EXPR);
        ParseTreeNode child3 = new ParseTreeNode(print, NodeType.END_PAREN);

        tokIndex ++;
        expandExpr(tokenList, child2);
        print.addChild(child1);
        print.addChild(child2);
        print.addChild(child3);

        //TODO:ERROR HANDLING
        tokIndex ++;
        // TODO: ERROR
        print.addChild(new ParseTreeNode(print, NodeType.END_STMT));
    }

    public static void traverseTree(ParseTreeNode node, int depth) {
        for(int i = 0; i < depth; ++i) {
            System.out.print("  ");
        }
        System.out.println(node.toString());
        if (!node.isLeafNode()) {
            for(ParseTreeNode child: node.getAllChildren()) {
                traverseTree(child, depth + 1);
            }
        }
    }
}
