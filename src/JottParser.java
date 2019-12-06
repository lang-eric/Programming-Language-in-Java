import javax.xml.soap.Node;
import java.sql.SQLOutput;
import java.util.*;

public class JottParser {

    private static String fileName;
    private static int tokIndex = 0;
    private static String cur_varName = "";
    private static NodeType cur_type = null;
    private static HashMap<String, NodeType> map = new HashMap<>();
    private static HashMap<String, JottFunction> func_map = new HashMap<>();
    private static ArrayList<JottFunction.Parameter> parameterList = new ArrayList<>();
    private static String cur_funcName = "";

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


    /**
     * Parses the token list.
     *
     * @param tokenList the list of tokens from JottTokenizer.
     * @param file_name the name of the file being interpreted. Used for error reporting.
     * @return a ParseTreeNode root that points to the rest of the tree.
     */
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

    /**
     * Takes a stmt_list node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmtList a node of type stmt_list
     */
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

    /**
     * Takes a stmt node and recursively populates it with child nodes. The field tokIndex determines which token
     * is being parsed, which in turn determines what the instructions will be in this statement.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmt a node of type stmt
     */
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
                if (stmt.getParent().getNodeType().equals(NodeType.FSTMT))
                    return;
                else
                    expandStmt(tokenList, stmt);
            }
        }

        else if (type.equals("if")) {
            expandIf(tokenList, stmt);
        }
        else if (type.equals("while")) {
            expandWhile(tokenList,stmt);
        }

        else if (type.equals("Void")) {
            stmt.addChild(new ParseTreeNode(stmt, NodeType.VOID));

            tokIndex ++;
            expandStmtWithFunc(tokenList, stmt);
        }

        else if (type.equals("type_Double")) {
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);
            expandASMT(tokenList, asmt, NodeType.DOUBLE);
            stmt.addChild(asmt);
        }

        else if (type.equals("type_Integer")) {
            ParseTreeNode asmt = new ParseTreeNode(stmt, NodeType.ASMT);
            if (tokenList.get(tokIndex + 1).getType().equals("lower_keyword")) {
                if (tokenList.get(tokIndex + 2).getType().equals("start_paren")) {
                    asmt = null;
                    stmt.addChild(new ParseTreeNode(stmt, NodeType.INTEGER));
                    tokIndex ++;
                    expandStmtWithFunc(tokenList, stmt);
                }
            }

            if (asmt == null) return;

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

    /**
     * Takes a bracket statement list node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmtList a node of type b_stmt_list
     */
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



    private static void expandStmtWithFunc(List<JottTokenizer.Token> tokenList, ParseTreeNode stmt) {
        String type = tokenList.get(tokIndex - 1).getType();
        NodeType t = NodeType.INTEGER;
        if (type.equals("type_Double")) t = NodeType.DOUBLE;
        if (type.equals("type_String")) t = NodeType.STRING;
        if (type.equals("Void")) t = NodeType.VOID;

        String func_name = tokenList.get(tokIndex).getValue();
        ParseTreeNode id = new ParseTreeNode(stmt, NodeType.ID);
        id.setValue(func_name);
        stmt.addChild(id);

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            if (tokenList.get(tokIndex).getType().equals("start_paren")) {
                stmt.addChild(new ParseTreeNode(stmt, NodeType.START_PAREN));
            }
        }

        tokIndex ++;
        if (tokIndex < tokenList.size()) {
            ParseTreeNode plst = new ParseTreeNode(stmt, NodeType.PLIST);
            expandPList(tokenList, plst);
            stmt.addChild(plst);
        }

        if (!tokenList.get(tokIndex).getType().equals("end_paren")) {
            System.out.println("Missing )");
            System.exit(-1);
        }

        if (tokenList.get(tokIndex).getType().equals("end_paren")) {
            JottFunction func = new JottFunction(func_name, t, parameterList, stmt);
            func_map.put(func_name, func);
            parameterList = new ArrayList<>();
            cur_funcName = func_name;
            stmt.addChild(new ParseTreeNode(stmt, NodeType.END_PAREN));
        }

        tokIndex ++;
        if (tokIndex >= tokenList.size()) {
            System.out.println("Missing function body");
            System.exit(-1);
        }

        if (!tokenList.get(tokIndex).getType().equals("start_blk")) {
            System.out.println("Missing {");
            System.exit(-1);
        }

        if (tokenList.get(tokIndex).getType().equals("start_blk")) {
            stmt.addChild(new ParseTreeNode(stmt, NodeType.START_BLK));
            ParseTreeNode fstmt = new ParseTreeNode(stmt, NodeType.FSTMT);
            tokIndex ++;
            expandFStmt(tokenList, fstmt);
            stmt.addChild(fstmt);
        }


        if (tokIndex < tokenList.size()) {
            if (tokenList.get(tokIndex).getType().equals("end_blk")) {
                stmt.addChild(new ParseTreeNode(stmt, NodeType.END_BLK));
                tokIndex ++;
            }

            else {
                System.out.println("Syntax Error: Missing end bulk, "+tokenList.get(tokIndex).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                System.exit(-1);
            }
        }

    }

    private static void expandFStmt(List<JottTokenizer.Token> tokenList, ParseTreeNode fstmt) {
        String type = tokenList.get(tokIndex).getType();

        if (type.equals("end_blk")) {
            fstmt.addChild(new ParseTreeNode(fstmt, NodeType.EPSILON));
        }

        else if (type.equals("return")) {
            fstmt.addChild(new ParseTreeNode(fstmt, NodeType.RETURN));

            tokIndex ++;
            ParseTreeNode expr = new ParseTreeNode(fstmt, NodeType.EXPR);
            expandExpr(tokenList, expr);
            fstmt.addChild(expr);

            tokIndex ++;
            expandFStmt(tokenList, fstmt);
        }

        else if (type.equals("end_stmt")){
            fstmt.addChild(new ParseTreeNode(fstmt, NodeType.END_STMT));
            tokIndex ++;
        }

        else {

            ParseTreeNode stmt = new ParseTreeNode(fstmt, NodeType.STMT);
            expandStmt(tokenList, stmt);
            fstmt.addChild(stmt);

            if (tokenList.get(tokIndex - 1).getType().equals("end_stmt")) {
                ParseTreeNode next = new ParseTreeNode(fstmt, NodeType.FSTMT);
                expandFStmt(tokenList, next);
                fstmt.addChild(next);
            }

            else if (tokenList.get(tokIndex).getType().equals("end_blk")) {
                fstmt.addChild(new ParseTreeNode(fstmt, NodeType.EPSILON));
            }

            else if (tokenList.get(tokIndex).getType().equals("return")) {
                fstmt.addChild(new ParseTreeNode(fstmt, NodeType.END_STMT));
                ParseTreeNode next = new ParseTreeNode(fstmt, NodeType.FSTMT);
                expandFStmt(tokenList, next);
                fstmt.addChild(next);
            }

            else {
                System.out.println("Error");
                System.exit(-1);
            }

        }

    }

    private static void expandPList(List<JottTokenizer.Token> tokenList, ParseTreeNode plst) {
        String type = tokenList.get(tokIndex).getType();
        ParseTreeNode param = new ParseTreeNode(plst, NodeType.INTEGER);
        if (type.equals("type_Double")) param = new ParseTreeNode(plst, NodeType.DOUBLE);
        if (type.equals("type_String")) param = new ParseTreeNode(plst, NodeType.STRING);
        plst.addChild(param);

        tokIndex ++;
        JottTokenizer.Token id = tokenList.get(tokIndex);
        if (id.getType().equals("lower_keyword")) {
            ParseTreeNode name = new ParseTreeNode(plst, NodeType.ID);
            name.setValue(id.getValue());
            plst.addChild(name);
            JottFunction.Parameter p = new JottFunction.Parameter(param.getNodeType(), id.getValue());
            parameterList.add(p);
        }

        tokIndex ++;
        if (tokenList.get(tokIndex).getType().equals("comma")) {
            ParseTreeNode c = new ParseTreeNode(plst, NodeType.COMMA);
            plst.addChild(c);

            tokIndex ++;
            ParseTreeNode child = new ParseTreeNode(plst, NodeType.PLIST);
            expandPList(tokenList, child);
            plst.addChild(child);
        }

        else if (tokenList.get(tokIndex).getType().equals("end_paren")) {
            return;
        }

        else {
            System.out.println("Syntax Error: Missing end paren, "+tokenList.get(tokIndex).line_string
                    + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }
    }

    private static void expandFCall(List<JottTokenizer.Token> tokenList, ParseTreeNode f_call) {
        ParseTreeNode id = new ParseTreeNode(f_call, NodeType.ID);
        expandId(tokenList, f_call);
        f_call.addChild(id);
        tokIndex++;
        addStartParen(f_call);
        tokIndex++;
        if(!tokenList.get(tokIndex).getType().equals("end_paren")) {
            ParseTreeNode fc_p_list = new ParseTreeNode(f_call, NodeType.FC_P_LIST);
            expandFCallParamList(tokenList, fc_p_list);
        }
        addEndParen(f_call);
        tokIndex++;
        if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
            System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex).line_string
                    + "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
            System.out.print("expandFCall");
            System.exit(-1);
        }
        f_call.addChild(new ParseTreeNode(f_call, NodeType.END_STMT));
        tokIndex++;
    }

    private static void expandFCallParamList(List<JottTokenizer.Token> tokenList, ParseTreeNode fc_p_list) {
        ParseTreeNode nextExpr = new ParseTreeNode(fc_p_list, NodeType.EXPR);
        expandExpr(tokenList, nextExpr);
        fc_p_list.addChild(nextExpr);
        tokIndex++;
        if(tokenList.get(tokIndex).getType().equals("comma")) {
            fc_p_list.addChild(new ParseTreeNode(fc_p_list, NodeType.COMMA));
            ParseTreeNode nextFCPList = new ParseTreeNode(fc_p_list, NodeType.FC_P_LIST);
            tokIndex++;
            expandFCallParamList(tokenList, nextFCPList);
            fc_p_list.addChild(nextFCPList);
        }
    }

    /**
     * Takes a bracket statement node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param b_stmt a node of type b_stmt
     */
    private static void expandBSTMT(List<JottTokenizer.Token> tokenList, ParseTreeNode b_stmt) {
        String type = tokenList.get(tokIndex).getType();
        if (type.equals("print")) {
            ParseTreeNode prt = new ParseTreeNode(b_stmt, NodeType.PRINT);
            expandPrint(tokenList, prt);
            b_stmt.addChild(prt);
            //tokIndex ++;
            if (tokenList.get(tokIndex).getType().equals("end_blk")){
                return;
            }
            tokIndex ++;
            if (tokenList.get(tokIndex - 2).getType().equals("end_stmt")) {
                tokIndex --;
            }
        }

        else if (type.equals("lower_keyword")) {
            ParseTreeNode prt = new ParseTreeNode(b_stmt, NodeType.RASMT);
            expandRAsmt(tokenList, prt);
            b_stmt.addChild(prt);
        }

        else if (type.equals("end_stmt")){
            tokIndex ++;
            if (tokIndex < tokenList.size() && !tokenList.get(tokIndex).getType().equals("end_blk")) {
                expandBSTMT(tokenList, b_stmt);
            }
        }

        else if (type.equals("end_blk")){
            return;
        }

        else if (type.equals("if")) {
            expandIf(tokenList, b_stmt);
        }

        else {
            ParseTreeNode prt = new ParseTreeNode(b_stmt, NodeType.EXPR);
            expandExpr(tokenList, prt);
            b_stmt.addChild(prt);
            tokIndex++;
            //TODO: check for missing end bracket error
            b_stmt.addChild(new ParseTreeNode(b_stmt, NodeType.END_STMT));
        }
    }

    /**
     * Takes a reassignment node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param r_asmt a node of type r_asmt
     */
    private static void expandRAsmt(List<JottTokenizer.Token> tokenList, ParseTreeNode r_asmt){
        String type = tokenList.get(tokIndex).getType();
        ParseTreeNode id = new ParseTreeNode(r_asmt, NodeType.ID);

        //tokIndex ++;
        expandId(tokenList, id);
        cur_type = NodeType.INTEGER;
        cur_varName = id.getValue();
        map.put(cur_varName, cur_type);
        r_asmt.addChild(id);

        tokIndex ++;
        ParseTreeNode expr = new ParseTreeNode(r_asmt, NodeType.EXPR);
        tokIndex ++;
        expandExpr(tokenList, expr);
        r_asmt.addChild(expr);


    }

    /**
     * Takes a statement node and recursively populates it with child nodes, parsing an if statement.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmt a node of type stmt
     */
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

    /**
     * Takes a statement node and recursively populates it with child nodes, parsing for an else statement.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmt a node of type stmt
     */
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

    /**
     * Takes an assignment node and recursively populates it with child nodes, depending on the data type provided.
     * @param tokenList the list of tokens from JottTokenizer
     * @param asmt the node of type asmt
     * @param type the data type of the variable being assigned
     */
    private static void expandASMT(List<JottTokenizer.Token> tokenList, ParseTreeNode asmt, NodeType type) {
        if (type.equals(NodeType.DOUBLE)) {
            ParseTreeNode child = new ParseTreeNode(asmt, NodeType.DOUBLE);
            asmt.addChild(child);
            tokIndex++;
            ParseTreeNode id = new ParseTreeNode(asmt, NodeType.ID);
            expandId(tokenList, id);
            cur_type = NodeType.DOUBLE;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            asmt.addChild(id);

            tokIndex++;
            if (!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Syntax Error: expected '=' got '" + tokenList.get(tokIndex).getValue() +
                        "', \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }

            ParseTreeNode child2 = new ParseTreeNode(asmt, NodeType.OP, "=");
            asmt.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(asmt, NodeType.D_EXPR);
            expandDExpr(tokenList, child3);
            asmt.addChild(child3);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 2).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 2).line + ") " + tokenList.get(tokIndex).getValue());
                System.out.print("expandASMT: case DOUBLE");
                System.exit(-1);
            }
            asmt.addChild(new ParseTreeNode(asmt, NodeType.END_STMT));
            tokIndex ++;
        }

        else if (type.equals(NodeType.INTEGER)) {
            ParseTreeNode child = new ParseTreeNode(asmt, NodeType.INTEGER);
            asmt.addChild(child);
            tokIndex++;
            ParseTreeNode id = new ParseTreeNode(asmt, NodeType.ID);
            expandId(tokenList, id);
            cur_type = NodeType.INTEGER;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            asmt.addChild(id);

            tokIndex++;
            if (!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Syntax Error: expected = got " + tokenList.get(tokIndex).getValue() +
                        ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }

            ParseTreeNode child2 = new ParseTreeNode(asmt, NodeType.OP, "=");
            asmt.addChild(child2);

            tokIndex++;
            ParseTreeNode child3 = new ParseTreeNode(asmt, NodeType.I_EXPR);
            expandIExpr(tokenList, child3);
            asmt.addChild(child3);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 2).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 2).line + ") " + tokenList.get(tokIndex).getValue());
                System.out.print("expandASMT: case INTEGER");
                System.exit(-1);
            }
            asmt.addChild(new ParseTreeNode(asmt, NodeType.END_STMT));
            tokIndex ++;
        }

        else if (type.equals(NodeType.STR)) {
            ParseTreeNode str = new ParseTreeNode(asmt, NodeType.STRING, "String");
            asmt.addChild(str);
            tokIndex += 1;
            ParseTreeNode id = new ParseTreeNode(asmt, NodeType.ID);
            expandId(tokenList, id);
            cur_type = NodeType.STR;
            cur_varName = id.getValue();
            map.put(cur_varName, cur_type);
            asmt.addChild(id);
            tokIndex += 1;

            if(!tokenList.get(tokIndex).getType().equals("assign")) {
                System.out.println("Syntax Error: expected = got " + tokenList.get(tokIndex).getValue() +
                        ", \"" + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" +
                        tokenList.get(tokIndex).line + ")");
                System.exit(-1);
            }
            ParseTreeNode assignment = new ParseTreeNode(asmt, NodeType.OP, "=");
            asmt.addChild(assignment);
            tokIndex += 1;

            ParseTreeNode s_expr = new ParseTreeNode(asmt, NodeType.S_EXPR);
            expandSExpr(tokenList, s_expr);
            asmt.addChild(s_expr);

            if(!tokenList.get(tokIndex).getType().equals("end_stmt")) {
                System.out.println("Syntax Error: missing end statement, \"" + tokenList.get(tokIndex - 1).line_string
                        + "\" (" + fileName + ":" + tokenList.get(tokIndex - 1).line + ") " + tokenList.get(tokIndex - 1).getValue());
                System.out.print("expandASMT: case STR");
                System.exit(-1);
            }
            asmt.addChild(new ParseTreeNode(asmt, NodeType.END_STMT));
            tokIndex += 1;
        }
    }

    /**
     * Takes an expression node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param expr the node of type expr
     */
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
                if (cur_funcName != null) {
                    ArrayList<JottFunction.Parameter> plst = func_map.get(cur_funcName).getParameters();
                    for (JottFunction.Parameter p : plst) {
                        if (p.getName().equals(tokenList.get(tokIndex).getValue())) {
                            type = p.getTYPE();
                            continue;
                        }
                    }
                }

                if (type == null) {
                    System.out.println("Syntax Error: variable not found at \"" + tokenList.get(tokIndex).line_string +
                            "\" (" + fileName + ":" + tokenList.get(tokIndex).line + ") " + tokenList.get(tokIndex).getValue());
                    System.exit(-1);
                }

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
                if (cur_funcName != null) {
                    ArrayList<JottFunction.Parameter> plst = func_map.get(cur_funcName).getParameters();
                    for (JottFunction.Parameter p : plst) {
                        if (p.getName().equals(tokenList.get(tokIndex).getValue())) {
                            type = p.getTYPE();
                            continue;
                        }
                    }
                }

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

    /**
     * Takes an integer expression node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param i_expr the node of type i_expr
     */
    private static void expandIExpr(List<JottTokenizer.Token> tokenList, ParseTreeNode i_expr) {
        String type = tokenList.get(tokIndex).getType();
        List<ParseTreeNode> children = i_expr.getAllChildren();


        // <id> case
        if (type.equals("lower_keyword")) {
            ParseTreeNode node = new ParseTreeNode(i_expr, NodeType.ID);
            expandId(tokenList, node);
            i_expr.addChild(node);
            tokIndex ++;
            expandIExpr(tokenList, i_expr);
        }

        else if (type.equals("integer")) {
            ParseTreeNode node = new ParseTreeNode(i_expr, NodeType.INT);
            expandDBL(tokenList, node);
            i_expr.addChild(node);
            tokIndex ++;
            expandIExpr(tokenList, i_expr);
        }

        else if (type.equals("start_paren")) {
            ParseTreeNode child1 = new ParseTreeNode(i_expr, NodeType.I_EXPR);
            tokIndex ++;
            expandIExpr(tokenList, child1);
            i_expr.addChild(child1);

            tokIndex ++;
            expandIExpr(tokenList, i_expr);
        }

        else if (type.equals("minus")) {

            if (!tokenList.get(tokIndex - 1).getValue().equals("+") && !tokenList.get(tokIndex - 1).getValue().equals("-") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("/") && !tokenList.get(tokIndex - 1).getValue().equals("*") &&
                    !tokenList.get(tokIndex - 1).getValue().equals("^") && !tokenList.get(tokIndex - 1).getValue().equals("(") &&
                    !tokenList.get(tokIndex - 1).getType().equals("print")) {
                ParseTreeNode op = new ParseTreeNode(i_expr, NodeType.OP, "-");


                if (children.size() > 1) {
                    if (children.size() == 3) {
                        ParseTreeNode child = new ParseTreeNode(i_expr, NodeType.I_EXPR);
                        children.get(0).setParent(child);
                        children.get(1).setParent(child);
                        children.get(2).setParent(child);
                        child.addChild(children.get(0));
                        child.addChild(children.get(1));
                        child.addChild(children.get(2));
                        i_expr.removeAllChild();

                        i_expr.addChild(child);
                        i_expr.addChild(op);

                        tokIndex ++;
                        expandIExpr(tokenList, i_expr);
                    }
                }


                else {
                    i_expr.addChild(op);
                }
            }
            if(!tokenList.get(tokIndex).getType().equals("end_paren"))
                tokIndex ++;
            expandIExpr(tokenList, i_expr);
        }

        else if (type.equals("plus") || type.equals("divide") || type.equals("power") || type.equals("mult") ||
                type.equals("greater") || type.equals("less") || type.equals("greater_eq") || type.equals("less_eq") ||
                type.equals("eq") || type.equals("not_eq")) {
            ParseTreeNode op = new ParseTreeNode(i_expr, NodeType.OP, "+");
            if (type.equals("divide")) op = new ParseTreeNode(i_expr, NodeType.OP, "/");
            if (type.equals("mult")) op = new ParseTreeNode(i_expr, NodeType.OP, "*");
            if (type.equals("power")) op = new ParseTreeNode(i_expr, NodeType.OP, "^");
            if (type.equals("greater")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, ">");
            if (type.equals("less")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, "<");
            if (type.equals("not_eq")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, "!=");
            if (type.equals("greater_eq")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, ">=");
            if (type.equals("less_eq")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, "<=");
            if (type.equals("eq")) op = new ParseTreeNode(i_expr, NodeType.REL_OP, "==");


            if (children.size() > 1) {
                if (children.size() == 3) {
                    ParseTreeNode child = new ParseTreeNode(i_expr, NodeType.I_EXPR);
                    children.get(0).setParent(child);
                    children.get(1).setParent(child);
                    children.get(2).setParent(child);
                    child.addChild(children.get(0));
                    child.addChild(children.get(1));
                    child.addChild(children.get(2));
                    i_expr.removeAllChild();

                    i_expr.addChild(child);
                    i_expr.addChild(op);

                    tokIndex ++;
                    expandIExpr(tokenList, i_expr);
                }
            }

            else {
                i_expr.addChild(op);
            }
            if(!tokenList.get(tokIndex).getType().equals("end_paren"))
                tokIndex ++;
            expandIExpr(tokenList, i_expr);
        }

        else if(isDataType(tokenList.get(tokIndex))) {
            System.out.println("Syntax Error: type mismatch, expected type Integer but found " + type + ", \""
                    + tokenList.get(tokIndex).line_string + "\" (" + fileName + ":" + tokenList.get(tokIndex).line
                    + ") " + tokenList.get(tokIndex).getValue());
            System.exit(-1);
        }

    }

    /**
     * Takes a statement node and recursively populates it with child nodes, parsing for a while statement.
     * @param tokenList the list of tokens from JottTokenizer
     * @param stmt the node of type stmt
     */
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

    /**
     * Takes a string expression node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param s_expr the node of type s_expr
     */
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

    /**
     * Expands a string literal node.
     * @param tokenList the list of tokens from JottTokenizer
     * @param str_literal the node of type str_literal
     */
    private static void expandStrLiteral(List<JottTokenizer.Token> tokenList, ParseTreeNode str_literal) {
        str_literal.addChild(new ParseTreeNode(str_literal, NodeType.QUOTE,"\""));
        ParseTreeNode str = new ParseTreeNode(str_literal, NodeType.STR);
        str_literal.addChild(str);
        str.addChild(new ParseTreeNode(str, NodeType.STR, tokenList.get(tokIndex).getValue()));
        str_literal.addChild(new ParseTreeNode(str_literal, NodeType.QUOTE, "\""));
    }

    /**
     * Takes a double expression node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param dexpr the node of type d_expr
     */
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
        else if (new HashSet<String>(Arrays.asList("plus","divide","power","mult","greater","less","greater_eq","eq","not_eq")).contains(type))
        {
            ParseTreeNode op;
            switch (type) {
                case "plus":
                    op = new ParseTreeNode(dexpr, NodeType.OP, "+");
                    break;
                case "divide":
                    op = new ParseTreeNode(dexpr, NodeType.OP, "/");
                    break;
                case "mult":
                    op = new ParseTreeNode(dexpr, NodeType.OP, "*");
                    break;
                case "power":
                    op = new ParseTreeNode(dexpr, NodeType.OP, "^");
                    break;
                case "greater":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">");
                    break;
                case "less":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<");
                    break;
                case "not_eq":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, "!=");
                    break;
                case "greater_eq":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, ">=");
                    break;
                case "less_eq":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, "<=");
                    break;
                case "eq":
                    op = new ParseTreeNode(dexpr, NodeType.REL_OP, "==");
                    break;
                default:             //op should not be null.
                    throw new IllegalStateException("Unexpected value: " + type);
            }
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

    /**
     * Takes an ID node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param id the node of type id
     */
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

        id.setLineString(tokenList.get(tokIndex).line_string);
        id.setLine_number(tokenList.get(tokIndex).line);
        id.setFileName(fileName);

        for (int i = 1; i < val.length(); i++) {
            //TODO: check for error
        }

        id.setValue(val);
    }

    /**
     * Parses a double.
     * @param tokenList the list of tokens from JottTokenizer
     * @param dbl the node of type dbl
     */
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
        dbl.setLineString(tokenList.get(tokIndex).line_string);
        dbl.setLine_number(tokenList.get(tokIndex).line);
        dbl.setFileName(fileName);
    }


    /**
     * Takes a print node and recursively populates it with child nodes.
     * @param tokenList the list of tokens from JottTokenizer
     * @param printNode the node of type print
     */
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

    /**
     * Checks if a token is a literal of a data type.
     * @param token the token to be inspected
     * @return true if the token is an integer, double, or string literal; false otherwise.
     */
    private static boolean isDataType(JottTokenizer.Token token) {
        return token.getType().equals("integer") || token.getType().equals("double") || token.getType().equals("string");
    }

    /**
     * Used for testing in early phase 1 for basic functionality. Now depreciated.
     * @param node the node to traverse from.
     * @param depth the depth of the current node being parsed. This gets recursively passed in so that the node's
     *              value can be indented to show the depth.
     */
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
