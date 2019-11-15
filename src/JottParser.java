import java.util.HashMap;
import java.util.List;

public class JottParser {

    private static String fileName;
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
     * 'else' - else
     * 'while(' - while
     * 'for(' - for
     * '{' - start_blk
     * '}' - end_blk
     *
     *
     * NodeType constants, as indicated in ParseTreeNode:
     * PROGRAM, STMT_LIST, $$, STMT, END_STMT, EXPR, START_PAREN, END_PAREN,
     *	CHAR, L_CHAR, U_CHAR, DIGIT, SIGN, ID, PRINT, ASMT, OP,
     *	DBL, DBL_EXPR, INT, I_EXPR, STR_LITERAL, STR, STR_EXPR
     */

    public JottParser(){
        this.tokIndex = 0;
    }



    public static ParseTreeNode parseTokens(List<JottTokenizer.Token> tokenList, String file_name) {
        fileName = file_name;
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

        else if (type.equals("end_stmt") || type.equals("end_paren")) {
            tokIndex ++;
            if (tokIndex < tokenList.size()) {
                expandStmt(tokenList, stmt);
            }
        }

        else if (type.equals("if")) {
            expandIf(tokenList, stmt);
        }
        else if (type.equals("while")) expandWhile(tokenList,stmt);

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

        else if (type.equals("lower_keyword")) {
            ParseTreeNode expr = new ParseTreeNode(stmt, NodeType.EXPR);
            if (tokenList.get(tokIndex + 1).getValue().equals("=")) {
                expr = new ParseTreeNode(stmt, NodeType.RASMT);
                expandRAsmt(tokenList, expr);
                stmt.addChild(expr);
            }
            else {
                expandExpr(tokenList, expr);
                stmt.addChild(expr);
            }
        }

        else if (type.equals("for")) {
            ParseTreeNode forLoopStart = new ParseTreeNode(stmt, NodeType.FOR);
            stmt.addChild(forLoopStart);
            tokIndex += 1;
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);

            switch(tokenList.get(tokIndex).getType()) {
                case "type_Double":
                    expandASMT(tokenList, asmt, NodeType.DOUBLE);
                    break;
                case "type_Integer":
                    expandASMT(tokenList, asmt, NodeType.INTEGER);
                    break;
                case "type_String":
                    expandASMT(tokenList, asmt, NodeType.STR);
                    break;
            }
            stmt.addChild(asmt);
            ParseTreeNode i_expr = new ParseTreeNode(stmt, NodeType.I_EXPR);
            expandIExpr(tokenList, i_expr);
            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 2).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 2).line + ")");
                System.exit(-1);
            }
            stmt.addChild(i_expr);
            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_STMT));
            tokIndex += 1;
            ParseTreeNode r_asmt = new ParseTreeNode(stmt, NodeType.RASMT);
            expandRAsmt(tokenList, r_asmt);
            stmt.addChild(r_asmt);

            if(!(tokenList.get(tokIndex).getType().equals("end_paren"))) {
                System.out.println("Syntax Error: missing closing parenthesis, \"" + tokenList.get(tokIndex - 1).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            addEndParen(stmt);
            tokIndex += 1;
            if(!(tokenList.get(tokIndex).getType().equals("start_blk"))) {
                System.out.println("Syntax Error: missing opening bracket, \"" + tokenList.get(tokIndex - 1).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            stmt.addChild(new ParseTreeNode(stmt, NodeType.START_BLK));
            tokIndex += 1;
            ParseTreeNode b_stmt_list = new ParseTreeNode(stmt, NodeType.B_STMT_LIST);
            expandBSTMTLIST(tokenList, b_stmt_list);
            stmt.addChild(b_stmt_list);

            if(!(tokenList.get(tokIndex).getType().equals("end_blk"))) {
                System.out.println("Syntax Error: missing closing bracket, \"" + tokenList.get(tokIndex - 1).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_BLK));
            tokIndex += 1;
        }

        else {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.EXPR);
            expandExpr(tokenList, prt);
            stmt.addChild(prt);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.out.print("expandSTMT");
                System.exit(-1);
            }
            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_STMT));
            tokIndex++;
        }

    }

    private static void expandBSTMTLIST(List<JottTokenizer.Token> tokenList, ParseTreeNode stmtList) {
        if (tokenList.get(tokIndex).getType().equals("end_blk")) {
            stmtList.addChild(new ParseTreeNode(stmtList, NodeType.EPSILON));
            return;
        }
        else {
            ParseTreeNode stmt = new ParseTreeNode(stmtList, NodeType.B_STMT);
            ParseTreeNode stmtListNext = new ParseTreeNode(stmtList, NodeType.B_STMT_LIST);
            expandBSTMT(tokenList, stmt);
            expandBSTMTLIST(tokenList, stmtListNext);
            stmtList.addChild(stmt);
            stmtList.addChild(stmtListNext);
        }
    }

    private static void expandBSTMT(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        String type = tokenList.get(tokIndex).getType();
        if (type.equals("print")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.PRINT);
            expandPrint(tokenList, prt);
            stmt.addChild(prt);
            //tokIndex ++;
            if (tokenList.get(tokIndex).getType().equals("end_blk")){
                return;
            }
            tokIndex ++;
        }

        else if (type.equals("lower_keyword")) {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.RASMT);
            expandRAsmt(tokenList, prt);
            stmt.addChild(prt);
        }

        else if (type.equals("end_stmt")){
            tokIndex ++;
            if (tokIndex < tokenList.size() && !tokenList.get(tokIndex).getType().equals("end_blk")) {
                expandBSTMT(tokenList, stmt);
            }
        }

        else if (type.equals("end_blk")){
            return;
        }

        else if (type.equals("if")) {
            expandIf(tokenList, stmt);
        }

        else {
            ParseTreeNode prt = new ParseTreeNode(stmt, NodeType.EXPR);
            expandExpr(tokenList, prt);
            stmt.addChild(prt);
            tokIndex++;
            //TODO: check for missing end bracket error
            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_STMT));
        }
    }

    private static void expandRAsmt(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt){
        String type = tokenList.get(tokIndex).getType();
        ParseTreeNode id = new ParseTreeNode(stmt, NodeType.ID);

        //tokIndex ++;
        expandId(tokenList, id);
        cur_type = NodeType.INTEGER;
        cur_varName = id.getValue();
        map.put(cur_varName, cur_type);
        stmt.addChild(id);

        tokIndex ++;
        ParseTreeNode expr = new ParseTreeNode(stmt, NodeType.EXPR);
        tokIndex ++;
        expandExpr(tokenList, expr);
        stmt.addChild(expr);


    }


    private static void expandIf(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        ParseTreeNode head = new ParseTreeNode(stmt, NodeType.IF);
        ParseTreeNode head_s = new ParseTreeNode(stmt, NodeType.START_PAREN);
        stmt.addChild(head);
        stmt.addChild(head_s);

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            String s = tokenList.get(tokIndex).getType();
            ParseTreeNode expr = new ParseTreeNode(stmt, NodeType.EXPR);
            if (s.equals("double") || s.equals("integer") || s.equals("string") || s.equals("lower_keyword")) {
                expandExpr(tokenList, expr);
                stmt.addChild(expr);
            }

//            else if (s.equals("lower_keyword")) {
//                if (tokenList.get(tokIndex).getValue().equals("=")) {
//                    expr = new ParseTreeNode(stmt, NodeType.RASMT);
//                    expandRAsmt(tokenList, expr);
//                    stmt.addChild(expr);
//                }
//                else {
//                    expandExpr(tokenList, expr);
//                    stmt.addChild(expr);
//                }
//            }

            else if (tokenList.get(tokIndex).getType().equals("if")) {
                ParseTreeNode nest_stmt = new ParseTreeNode(stmt, NodeType.STMT);
                expandIf(tokenList, nest_stmt);
                expr.addChild(nest_stmt);
                stmt.addChild(expr);
            }

            else {
                //TODO: ERROR HANDLING
                System.out.println("ERROR: condition for if stmt");
                System.exit(-1);
            }
        }

        //tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("end_paren")) {
                System.out.println("Syntax Error: Missing end paren, "+tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.END_PAREN);
            stmt.addChild(head_e);
        }

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("start_blk")) {
                System.out.println("Syntax Error: Missing start_blk, " + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.START_BLK);
            stmt.addChild(head_e);
        }

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            ParseTreeNode blst = new ParseTreeNode(stmt, NodeType.B_STMT_LIST);
            expandBSTMTLIST(tokenList, blst);
            stmt.addChild(blst);
        }

        //tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("end_blk")) {
                System.out.println("Syntax Error: Missing end_blk, " + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.END_BLK);
            stmt.addChild(head_e);
            tokIndex ++;
            if (tokIndex < tokenList.size()) {
                if (!tokenList.get(tokIndex).getValue().equals("else"))
                    return;
            }
        }

        if (tokIndex == tokenList.size()) {
            return;
        }

        //tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (tokenList.get(tokIndex).getValue().equals("else")) {
                stmt.addChild(new ParseTreeNode(stmt, NodeType.ELSE));
                tokIndex ++;
                expandElse(tokenList, stmt);
                tokIndex ++;
                //stmt.addChild(else_stmt);
            }

            else if (tokenList.get(tokIndex).getType().equals("end_blk")) {
                return;
            }

            else {
                System.out.println("Syntax Error: additional input, " + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
        }


        else {
            //TODO: ERROR HANDLING
            return;
        }
    }

    private static void expandElse(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        ParseTreeNode head_s = new ParseTreeNode(stmt, NodeType.START_BLK);
        stmt.addChild(head_s);

        tokIndex++;
        if (tokIndex < tokenList.size()) {
            ParseTreeNode blst = new ParseTreeNode(stmt, NodeType.B_STMT_LIST);
            expandBSTMTLIST(tokenList, blst);
            stmt.addChild(blst);
        }

        //tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (tokenList.get(tokIndex).getType().equals("end_blk")) {
                ParseTreeNode end = new ParseTreeNode(stmt, NodeType.END_BLK);
                stmt.addChild(end);
                return;
            }
        }

        else {
            System.out.println("Syntax Error: " + tokenList.get(tokIndex).line_string);
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
                System.out.println("Syntax Error: expected '=' got '" + tokenList.get(tokIndex).getValue() +
                        "', \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }

            ParseTreeNode child2 = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(parent, NodeType.D_EXPR);
            expandDExpr(tokenList, child3);
            parent.addChild(child3);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 2).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 2).line + ") " + tokenList.get(tokIndex).getValue());
                System.out.print("expandASMT: case DOUBLE");
                System.exit(-1);
            }
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
                System.out.println("Syntax Error: expected = got " + tokenList.get(tokIndex).getValue() +
                        ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }

            ParseTreeNode child2 = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(parent, NodeType.I_EXPR);
            expandIExpr(tokenList, child3);
            parent.addChild(child3);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 2).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 2).line + ") " + tokenList.get(tokIndex).getValue());
                System.out.print("expandASMT: case INTEGER");
                System.exit(-1);
            }
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
                System.out.println("Syntax Error: expected = got " + tokenList.get(tokIndex).getValue() +
                        ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }
            ParseTreeNode assignment = new ParseTreeNode(parent, NodeType.OP, "=");
            parent.addChild(assignment);
            tokIndex += 1;

            ParseTreeNode s_expr = new ParseTreeNode(parent, NodeType.S_EXPR);
            expandSExpr(tokenList, s_expr);
            parent.addChild(s_expr);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 1).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex - 1).getValue());
                System.out.print("expandASMT: case STR");
                System.exit(-1);
            }
            parent.addChild(new ParseTreeNode(parent, NodeType.END_STMT));
            tokIndex += 1;
        }
    }

    private static void expandExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode expr) {
        if (tokenList.get(tokIndex).getType().equals("integer")){
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.I_EXPR);
            expandIExpr(tokenList, ptr);
            expr.addChild(ptr);
        }

        else if (tokenList.get(tokIndex).getType().equals("double")) {
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.D_EXPR);
            expandDExpr(tokenList, ptr);
            expr.addChild(ptr);
        }

        else if (tokenList.get(tokIndex).getType().equals("string") || tokenList.get(tokIndex).getType().equals("concat")
                || tokenList.get(tokIndex).getType().equals("charAt")) {
            ParseTreeNode s_expr = new ParseTreeNode(expr, NodeType.S_EXPR);
            expandSExpr(tokenList, s_expr);
            expr.addChild(s_expr);
        }

        else if (tokenList.get(tokIndex).getType().equals("lower_keyword")) {
            ParseTreeNode ptr = new ParseTreeNode(expr, NodeType.ID);

            if (tokenList.get(tokIndex + 1).getType().equals("plus") || tokenList.get(tokIndex + 1).getType().equals("mult") ||
                    tokenList.get(tokIndex + 1).getType().equals("minus") || tokenList.get(tokIndex + 1).getType().equals("divide") ||
                    tokenList.get(tokIndex + 1).getType().equals("power") || tokenList.get(tokIndex + 1).getType().equals("less") ||
                    tokenList.get(tokIndex + 1).getType().equals("greater") || tokenList.get(tokIndex + 1).getType().equals("greater_eq") ||
                    tokenList.get(tokIndex + 1).getType().equals("less_eq") || tokenList.get(tokIndex + 1).getType().equals("eq") ||
                    tokenList.get(tokIndex + 1).getType().equals("not_eq")){
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
                    System.out.println("Syntax Error: variable not found at \"" + tokenList.get(tokIndex).line_string +
                            "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                    System.exit(-1);
                }
            }

            else {
                NodeType type = map.get(tokenList.get(tokIndex).getValue());
                if (type == null) {
                    JottTokenizer.Token tikToken = tokenList.get(tokIndex);
                    System.out.println("Syntax Error: Missing type declaration in line \"" +
                            "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") ");
                    System.exit(-1);
                }
                ptr = new ParseTreeNode(expr, NodeType.ID);
                expandId(tokenList, ptr);
                expr.addChild(ptr);
            }
        }

        else if (tokenList.get(tokIndex).getType().equals("minus")) {
            if (tokIndex + 1  == tokenList.size()) {
                JottTokenizer.Token tikToken = tokenList.get(tokIndex);
                System.out.println("Syntax Error: incomplete statement \"" + tikToken.line_string +
                        "\" (" + fileName + ":" + tikToken.line + ")");
                System.exit(-1);
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
                }


                else {
                    dexpr.addChild(op);
                }
            }
            if(!tokenList.get(tokIndex).getType().equals("end_paren"))
                tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if (type.equals("plus") || type.equals("divide") || type.equals("power") || type.equals("mult") ||
                type.equals("greater") || type.equals("less") || type.equals("greater_eq") || type.equals("less_eq") ||
                type.equals("eq") || type.equals("not_eq")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "+");
            if (type.equals("divide")) op = new ParseTreeNode(dexpr, NodeType.OP, "/");
            if (type.equals("mult")) op = new ParseTreeNode(dexpr, NodeType.OP, "*");
            if (type.equals("power")) op = new ParseTreeNode(dexpr, NodeType.OP, "^");
            if (type.equals("greater")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">");
            if (type.equals("less")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<");
            if (type.equals("not_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "!=");
            if (type.equals("greater_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">=");
            if (type.equals("less_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<=");
            if (type.equals("eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "==");


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
            }

            else {
                dexpr.addChild(op);
            }
            if(!tokenList.get(tokIndex).getType().equals("end_paren"))
                tokIndex ++;
            expandIExpr(tokenList, dexpr);
        }

        else if(isDataType(tokenList.get(tokIndex))) {
            System.out.println("Syntax Error: type mismatch, expected type Integer but found " + type + ", \""
                    + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" + tokenList.get(tokIndex).line
                    + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }

    }
    private static void expandWhile(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        ParseTreeNode head = new ParseTreeNode(stmt, NodeType.WHILE);
        ParseTreeNode head_s = new ParseTreeNode(stmt, NodeType.START_PAREN);
        stmt.addChild(head);
        stmt.addChild(head_s);

        tokIndex++;
        //we have parsed one token, while( ,which corresponds to two parse tree elements.
        if (tokIndex < tokenList.size()) { //instead of if statements, we could have a method that just checks this condition, and errors otherwise, to make the code 100000X more readable.
            String s = tokenList.get(tokIndex).getType();
            ParseTreeNode expr = new ParseTreeNode(stmt, NodeType.EXPR);
            if (s.equals("double") || s.equals("integer") || s.equals("string") || s.equals("lower_keyword")) {
                expandExpr(tokenList, expr);
                stmt.addChild(expr);
            }
            else {
                //TODO: ERROR HANDLING
                System.out.println("ERROR: condition for while stmt");
                System.exit(-1);
            }
        }
        else{
            System.out.println("ERROR: While loop ended without body.");
            System.exit(-1);
        }

        //tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("end_paren")) {
                System.out.println("Syntax Error: Missing end paren \"" + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") ");
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.END_PAREN);
            stmt.addChild(head_e);
        }

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("start_blk")) {
                System.out.println("Syntax Error: Missing start_blk \"" + tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.START_BLK);
            stmt.addChild(head_e);
        }

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            ParseTreeNode blst = new ParseTreeNode(stmt, NodeType.B_STMT_LIST);
            expandBSTMTLIST(tokenList, blst);
            stmt.addChild(blst);
        }
        if (tokIndex < tokenList.size()) {
            if (!tokenList.get(tokIndex).getType().equals("end_blk")) {
                System.out.println("Syntax Error: Missing end_blk" + tokenList.get(tokIndex).line_string);
                System.exit(-1);
            }
            ParseTreeNode head_e = new ParseTreeNode(stmt, NodeType.END_BLK);
            stmt.addChild(head_e);
            tokIndex ++;
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
                    System.out.println("Syntax Error: expected , got " + tokenList.get(tokIndex).getValue() +
                            ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                            tokenList.get(tokIndex).line + ")");
                    System.exit(-1);
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
                    System.out.println("Syntax Error: expected , got " + tokenList.get(tokIndex).getValue() +
                            ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                            tokenList.get(tokIndex).line + ")");
                    System.exit(-1);
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
            default:
                System.out.println("Syntax Error: type mismatch, expected type String but found " + sExprType + ", \""
                        + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" + tokenList.get(tokIndex).line
                        + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
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
                }


                else {
                    dexpr.addChild(op);
                }
            }

            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if (type.equals("plus") || type.equals("divide")  || type.equals("power") || type.equals("mult") ||
                type.equals("greater") || type.equals("less") || type.equals("greater_eq") || type.equals("less_eq") ||
                type.equals("eq") || type.equals("not_eq")) {
            ParseTreeNode op = new ParseTreeNode(dexpr, NodeType.OP, "+");
            if (type.equals("divide")) op = new ParseTreeNode(dexpr, NodeType.OP, "/");
            if (type.equals("mult")) op = new ParseTreeNode(dexpr, NodeType.OP, "*");
            if (type.equals("power")) op = new ParseTreeNode(dexpr, NodeType.OP, "^");
            if (type.equals("greater")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">");
            if (type.equals("less")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<");
            if (type.equals("not_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "!=");
            if (type.equals("greater_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">=");
            if (type.equals("less_eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<=");
            if (type.equals("eq")) op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<=");

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
            }

            else {
                dexpr.addChild(op);
            }

            tokIndex ++;
            expandDExpr(tokenList, dexpr);
        }

        else if(isDataType(tokenList.get(tokIndex))){
            System.out.println("Syntax Error: type mismatch, expected type Double but found " + type + ", \""
                    + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" + tokenList.get(tokIndex).line
                    + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }
    }


    private static void expandId(List<JottTokenizer.Token> tokenList, ParseTreeNode id) {
        String val = tokenList.get(tokIndex).getValue();
        String res = "";
        if (Character.isUpperCase(val.charAt(0))) {
            JottTokenizer.Token tikToken = tokenList.get(tokIndex);
            System.out.println("Syntax Error: ID cannot begin with an uppercase letter, \"" +
                    tikToken.line_string + "\" (" + fileName + ":" + tikToken.line + ")");
            System.exit(-1);
        }
        res += val.charAt(0);


        for (int i = 1; i < val.length(); i++) {
            //TODO: check for error
        }

        id.setValue(val);
    }


    private static void expandDBL(List<JottTokenizer.Token> tokenList, ParseTreeNode dbl) {
        String val = tokenList.get(tokIndex).getValue();
        ParseTreeNode child = null;
        String sign = tokenList.get(tokIndex - 1).getType();
        String newSign = "";

        if (sign.equals("minus")) {
            if (tokenList.get(tokIndex - 2).getType().equals("plus") || tokenList.get(tokIndex - 2).getType().equals("minus") ||
                    tokenList.get(tokIndex - 2).getType().equals("mult") || tokenList.get(tokIndex - 2).getType().equals("divide") ||
                    tokenList.get(tokIndex - 2).getType().equals("power") || tokenList.get(tokIndex - 2).getType().equals("start_paren") ||
                    tokenList.get(tokIndex - 2).getType().equals("print") || tokenList.get(tokIndex - 2).getType().equals("greater") ||
                    tokenList.get(tokIndex - 2).getType().equals("greater_eq") || tokenList.get(tokIndex - 2).getType().equals("less") ||
                    tokenList.get(tokIndex - 2).getType().equals("less_eq") || tokenList.get(tokIndex - 2).getType().equals("not_eq") ||
                    tokenList.get(tokIndex - 2).getType().equals("eq") ) {

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



    private static void expandPrint(List<JottTokenizer.Token> tokenList, ParseTreeNode printNode) {
        ParseTreeNode child1 = new ParseTreeNode(printNode, NodeType.START_PAREN);
        ParseTreeNode child2 = new ParseTreeNode(printNode, NodeType.EXPR);
        ParseTreeNode child3 = new ParseTreeNode(printNode, NodeType.END_PAREN);
        ParseTreeNode child4 = new ParseTreeNode(printNode, NodeType.END_STMT);

        tokIndex++;
        expandExpr(tokenList, child2);
        printNode.addChild(child1);
        printNode.addChild(child2);
        if(child2.getChild(NodeType.ID) != null)
            tokIndex++;
        if(!(tokenList.get(tokIndex).getType().equals("end_paren"))) {
            System.out.println("Syntax Error: missing closing parenthesis, \"" + tokenList.get(tokIndex - 1).line_string
                    + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }
        printNode.addChild(child3);
        tokIndex++;
        if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
            System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 1).line_string
                    + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }
        printNode.addChild(new ParseTreeNode(printNode, NodeType.END_STMT));
        tokIndex++;
    }

    private static boolean isDataType(JottTokenizer.Token token) {
        return token.getType().equals("integer") || token.getType().equals("double") || token.getType().equals("string");
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
