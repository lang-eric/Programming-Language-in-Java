import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JottEvaluation {

    private static List<String> outputs = new ArrayList<>();
    private static HashMap<String, Variable> map = new HashMap<>();
    private static String varName = "";
    private static int isLoopCond = 0;

    public static List<String> JottEvaluation(ParseTreeNode tree) {
        if (tree.getAllChildren().size() == 0) {
            return null;
        }
        stmtListEval(tree.getAllChildren().get(0));

        return outputs;
    }

    public static String arithmeticOp(Object obj1, Object obj2, String op, String line) {
        int ans = 0;
        double ans_double = 0;

//        if (obj1 instanceof Integer && !(obj2 instanceof Integer)) {
//            System.out.println("Syntax Error: Type mismatch: Expected Integer got Double, " + line);
//            System.exit(-1);
//        }

//        if (obj1 instanceof Double && !(obj2 instanceof Double)) {
//            System.out.println("Syntax Error: Type mismatch: Expected Double got Integer " + line);
//            System.exit(-1);
//        }
        //Syntax Error: Type mismatch: Expected Integer got Double, "print( x + y );" (inputs/prog3.j:4)

        if (op.equals("+")) {
            if (obj1 instanceof Integer) {
                ans = (int) obj1 + (int) obj2;
                return String.valueOf(ans);
            } else {
                ans_double = (double) obj1 + (double) obj2;
                return String.valueOf(ans_double);
            }
        } else if (op.equals("-")) {
            if (obj1 instanceof Integer) {
                ans = (int) obj1 - (int) obj2;
                return String.valueOf(ans);
            } else {
                ans_double = (double) obj1 - (double) obj2;
                return String.valueOf(ans_double);
            }
        } else if (op.equals("*")) {
            if (obj1 instanceof Integer) {
                ans = (int) obj1 * (int) obj2;
                return String.valueOf(ans);
            } else {
                ans_double = (double) obj1 * (double) obj2;
                return String.valueOf(ans_double);
            }
        } else if (op.equals("^")) {
            if (obj1 instanceof Integer) {
                ans_double = Math.pow((int) obj1, (int) obj2);
                if (ans_double > 0) {
                    ans = (int) Math.round(ans_double);
                    return String.valueOf(ans);
                }
                return String.valueOf(ans_double);
            } else {
                ans_double = Math.pow((double) obj1, (double) obj2);
            }
            return String.valueOf(ans_double);
        } else {

            if (obj1 instanceof Integer) {
                try {
                    ans_double = (int) obj1 / (int) obj2;
                    ans = (int) Math.round(ans_double);
                    return String.valueOf(ans);

                } catch (Exception e) {
                    System.out.println("Runtime Error: Divide by zero, " + line);
                    System.exit(-1);
                }
                return String.valueOf(ans_double);
            } else {
                try {
                    ans_double = (double) obj1 / (double) obj2;

                } catch (Exception e) {
                    System.out.println("zero can not be divided...");
                }
                return String.valueOf(ans_double);
            }
        }
    }

    public static String booleanOp(Object obj1, Object obj2, String op) {
        if (obj1 instanceof Integer && !(obj2 instanceof Integer) ||
                obj1 instanceof Double && !(obj2 instanceof Double)) {
            System.out.println("Error: Comparison of different type of object");
            System.exit(-1);
        }

        if (op.equals(">")) {
            if (obj1 instanceof Integer) {
                if ((int) obj1 > (int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 > (double) obj2) return "1";
                return "0";
            }
        }

        else if (op.equals(">=")) {
            if (obj1 instanceof Integer) {
                if ((int) obj1 >=(int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 >= (double) obj2) return "1";
                return "0";
            }
        }

        else if (op.equals("<")) {
            if (obj1 instanceof Integer) {
                if ((int) obj1 < (int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 < (double) obj2) return "1";
                return "0";
            }
        }

        else if (op.equals("<=")) {
            if (obj1 instanceof Integer) {
                if ((int) obj1 <= (int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 <= (double) obj2) return "1";
                return "0";
            }
        }

        else if (op.equals("==")) {
            if (obj1 instanceof Integer) {
                if ((int) obj1 == (int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 == (double) obj2) return "1";
                return "0";
            }
        }

        else {
            if (obj1 instanceof Integer) {
                if ((int) obj1 != (int) obj2) return "1";
                return "0";
            }
            else {
                if ((double) obj1 != (double) obj2) return "1";
                return "0";
            }
        }
    }


    public static void stmtEval(ParseTreeNode tree){
        List<ParseTreeNode> children = tree.getAllChildren();
        if (children.size() == 0) return;
        if (children.get(0).getNodeType().equals(NodeType.ASMT)) {
            varName = children.get(0).getAllChildren().get(1).getValue();
            ASMTEval(children.get(0));
            varName = "";
        }

        else if (children.get(0).getNodeType().equals(NodeType.STMT)) {
            stmtEval(children.get(0));
        }

        else if (children.get(0).getNodeType().equals(NodeType.PRINT)) {
            varName = "";
            printEval(children.get(0));
        }

        else if (children.get(0).getNodeType().equals(NodeType.RASMT)) {
            RasmtEval(children.get(0));
        }

        else if (children.get(0).getNodeType().equals(NodeType.IF)) {
            int isTrue = ifConditionEval(children.get(2));
            if (isTrue == 1) {
                BStmtListEval(children.get(5));
            }
            else {
                if (children.size() > 7) {
                    BStmtListEval(children.get(9));
                }
            }
        }

        else if (children.get(0).getNodeType().equals(NodeType.FOR)) {
            /*
                0 - for
                1 - asmt
                2 - i_expr (the looping condition)
                3 - end_stmt (end the looping condition)
                4 - rasmt (do after each loop)
                5 - end_paren (close the head)
                6 - start_blk (start the body)
                7 - b_stmt_list (body)
                8 - end_blk (close the body)
             */
            ASMTEval(children.get(1));
//            List<ParseTreeNode> i_expr_list = children.get(2).getAllChildren();
//            ParseTreeNode var = i_expr_list.get(0);
//            int varVal = Integer.parseInt(map.get(var.getValue()).getValue());
//            String rel_op = i_expr_list.get(1).getValue();
//            int i_expr = Integer.parseInt(i_expr_list.get(2).getValue());
            while(ifConditionEval(children.get(2)) > 0) {
                BStmtListEval(children.get(7));
                RasmtEval(children.get(4));
            }
            isLoopCond = 0;
        }

        else {

        }
    }

    public static int ifConditionEval(ParseTreeNode tree) {
        String ans = intEval(tree.getAllChildren().get(0));
        if (tree.getNodeType().equals(NodeType.I_EXPR) || tree.getNodeType().equals(NodeType.D_EXPR)) {
            isLoopCond = 1;
            ans = intEval(tree);
        }
        int isTrue = Integer.parseInt(ans);
        return isTrue;
    }

    public static void BStmtListEval(ParseTreeNode tree) {
        List<ParseTreeNode> children = tree.getAllChildren();
        if (children.get(0).getNodeType().equals(NodeType.B_STMT)) {
            BStmtEval(children.get(0));
            BStmtListEval(children.get(1));
        }

        else if (children.get(0).getNodeType().equals(NodeType.B_STMT_LIST)) {
            BStmtListEval(children.get(0));
        }

        else if (children.get(0).getNodeType().equals(NodeType.EPSILON)) {
            return;
        }
    }

    public static void BStmtEval(ParseTreeNode tree) {
        if(!tree.getAllChildren().isEmpty()) {
            ParseTreeNode child = tree.getAllChildren().get(0);
            if (child.getNodeType().equals(NodeType.PRINT)) {
                printEval(child);
            } else if (child.getNodeType().equals(NodeType.RASMT)) {
                RasmtEval(child);
            } else if (child.getNodeType().equals(NodeType.IF)) {
                int isTrue = ifConditionEval(tree.getAllChildren().get(2));
                if (isTrue == 1) {
                    BStmtListEval(tree.getAllChildren().get(5));
                } else {
                    if (tree.getAllChildren().size() > 7) {
                        BStmtListEval(tree.getAllChildren().get(9));
                    }
                }
            } else if (child.getNodeType().equals(NodeType.I_EXPR)) {
                intEval(tree);
            } else if (child.getNodeType().equals(NodeType.D_EXPR)) {
                doubleEval(child);
            } else if (child.getNodeType().equals(NodeType.S_EXPR)) {
                stringEval(child);
            }
        }
    }

    public static void RasmtEval(ParseTreeNode tree) {
        String ans = "";
        List<ParseTreeNode> children = tree.getAllChildren();
        String var_name = children.get(0).getValue();
        ParseTreeNode expr = children.get(1);

        if (!map.containsKey(var_name)) {
            System.out.println("Variable not initialized...");
        }

        Variable val = map.get(var_name);

        if (expr.getNodeType().equals(NodeType.EXPR)) {
            expr = expr.getAllChildren().get(0);
            if (expr.getNodeType().equals(NodeType.D_EXPR)) {
                ans = doubleEval(expr);
                if (!val.getType().equals("double")) {
                    String type = "";
                    if (val.getType().equals("string")) {
                        type = "String";
                    }
                    else type = "Integer";
                    System.out.println("Syntax Error: Invalid type in re-assignment: Expected " + type + " got Double" + ", " +
                            "\"" + children.get(0).getLineString() + "\"" + " (" + children.get(0).getFileName() + ":" + children.get(0).getLine_number() + ")");
                    System.exit(-1);
                }
                map.put(var_name, new Variable(var_name, "double", ans));
            }

            else if (expr.getNodeType().equals(NodeType.I_EXPR)) {
                ans = intEval(expr);
                if (!val.getType().equals("int")) {
                    String type = "";
                    if (val.getType().equals("string")) {
                        type = "String";
                    }
                    else type = "Double";
                    System.out.println("Syntax Error: Invalid type in re-assignment: Expected " + type + " got Integer" + ", " +
                            "\"" + children.get(0).getLineString() + "\"" + " (" + children.get(0).getFileName() + ":" + children.get(0).getLine_number() + ")");
                    System.exit(-1);
                }
                map.put(var_name, new Variable(var_name, "int", ans));
            }

            else {
                ans = stringEval(expr);
                if (!val.getType().equals("string")) {
                    String type = "";
                    if (val.getType().equals("double")) {
                        type = "Double";
                    }
                    else type = "Integer";
                    System.out.println("Syntax Error: Invalid type in re-assignment: Expected " + type + " got String" + ", " +
                            "\"" + children.get(0).getLineString() + "\"" + " (" + children.get(0).getFileName() + ":" + children.get(0).getLine_number() + ")");
                    System.exit(-1);
                }
                map.put(var_name, new Variable(var_name, "string", ans));
            }
        }


    }

    public static void stmtListEval(ParseTreeNode tree){
        List<ParseTreeNode> children = tree.getAllChildren();

        if (children.size() == 0) return;

        if (children.get(0).getNodeType().equals(NodeType.STMT)) {
            stmtEval(children.get(0));
            stmtListEval(children.get(1));
        }


    }

    public static void ASMTEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String id = children.get(1).getValue();
        NodeType type = children.get(0).getNodeType();
        String ans = "";
        if (type.equals(NodeType.DOUBLE)) {
            ans = doubleEval(children.get(3));
        }

        else if (type.equals(NodeType.INTEGER)) {
            ans = intEval(children.get(3));
        }

        else {
            ans = stringEval(children.get(3));
        }

    }

    public static String stringEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String ans = "";
        NodeType type = tree.getNodeType();
        if(tree.getNodeType().equals(NodeType.STR)) {
            if(tree.getValue() == null) {
                ans = children.get(0).getValue();
            } else {
                ans = tree.getValue();
            }
        } else if(type.equals(NodeType.STR_LITERAL)) {
            ans = children.get(1).getAllChildren().get(0).getValue();
        }

        else if(type.equals(NodeType.S_EXPR)) {
            ParseTreeNode indicator = children.get(0);
            if(indicator.getNodeType().equals(NodeType.ID)) {
                ans = map.get(indicator.getValue()).getValue();
            }
            else if(indicator.getNodeType().equals(NodeType.STR_LITERAL) || indicator.getNodeType().equals(NodeType.STR)) {
                ans = stringEval(indicator);
            }
            else if(children.get(0).getNodeType().equals(NodeType.CONCAT)) {
                ParseTreeNode s_expr1 = children.get(2);
                ParseTreeNode s_expr2 = children.get(4);
                String s1 = stringEval(s_expr1);
                String s2 = stringEval(s_expr2);
                ans = s1.concat(s2);
            }
            else if(indicator.getNodeType().equals(NodeType.CHARAT)) {
                ParseTreeNode s_expr = children.get(2);
                ParseTreeNode i_expr = children.get(4);
                String str = stringEval(s_expr);
                String indexAsString = intEval(i_expr);
                int index = Integer.parseInt(indexAsString);
                ans = String.valueOf(str.charAt(index));
            }
            if (isLoopCond == 1) return ans;
            map.put(varName, new Variable(varName, "string", ans));
        }
        return ans;
    }

    public static String intEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String ans = "";
        if (tree.getNodeType().equals(NodeType.INT) || tree.getNodeType().equals(NodeType.OP) || tree.getNodeType().equals(NodeType.REL_OP)) {
            return tree.getValue();
        }
        else if (tree.getNodeType().equals(NodeType.I_EXPR)) {
            if (nums != 0) {
                if (nums == 1) {
                    ans = children.get(0).getValue();
                    if(tree.getParent().getNodeType().equals(NodeType.ASMT))
                        varName = tree.getParent().getAllChildren().get(1).getValue();
                    map.put(varName, new Variable(varName, "int", ans));
                    return children.get(0).getValue();
                }
                String left = intEval(children.get(0));
                String op = intEval(children.get(1));
                String right = intEval(children.get(2));
                ParseTreeNode child = children.get(2);

                String line_str = "\"" + child.getLineString() + "\"" + " (" + child.getFileName() + ":" + child.getLine_number() + ")";


                int d1 = 0;
                int d2 = 0;
                try {
                    d1 = Integer.parseInt(left);
                    d2 = Integer.parseInt(right);
                } catch (NumberFormatException nfe) {
                }
                if (op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=") ||
                    op.equals("==") || op.equals("!=")) {
                    ans = booleanOp(d1, d2, op);
                }

                if (op.equals("+") || op.equals("-") || op.equals("/") || op.equals("*") || op.equals("^")) {
                    ans = arithmeticOp(d1, d2, op, line_str);
                }
                if (isLoopCond == 1) return ans;
                map.put(varName, new Variable(varName, "int", ans));
            }
            //TODO:ERROR
            return ans;
        }
        else {
            if (map.get(tree.getValue()).getType().equals("double")) {
                String line_str = "\"" + tree.getLineString() + "\"" + " (" + tree.getFileName() + ":" + tree.getLine_number() + ")";
                System.out.println("Syntax Error: Type mismatch: Expected Integer got Double, " + line_str);
                System.exit(-1);
            }
            return map.get(tree.getValue()).getValue();
        }
    }


    public static void printEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        NodeType type = children.get(1).getAllChildren().get(0).getNodeType();
        String ans = "";
        if (type.equals(NodeType.D_EXPR)) {
            ans = doubleEval(children.get(1).getAllChildren().get(0));
        }

        else if (type.equals(NodeType.I_EXPR)) {
            ans = intEval(children.get(1).getAllChildren().get(0));
        }

        else if (type.equals(NodeType.S_EXPR)) {
            ans = stringEval(children.get(1).getAllChildren().get(0));
        }

        else {
            Variable var = map.get(children.get(1).getAllChildren().get(0).getValue());
            if (var == null) {
                //TODO: ERROR
            }
            else
                ans = var.getValue();
        }
        outputs.add(ans);

    }


    public static String doubleEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String ans = "";
        if (tree.getNodeType().equals(NodeType.DBL) || tree.getNodeType().equals(NodeType.OP) || tree.getNodeType().equals(NodeType.REL_OP)) {
            return tree.getValue();
        }

        else if (tree.getNodeType().equals(NodeType.D_EXPR)) {
            if (nums != 0) {
                if (nums == 1) {
                    ans = children.get(0).getValue();
                    map.put(varName, new Variable(varName, "double", ans));
                    return children.get(0).getValue();
                }

                String left = doubleEval(children.get(0));
                String op = doubleEval(children.get(1));
                String right = doubleEval(children.get(2));
                String line_str = children.get(2).getLineString();

                Double d1 = Double.parseDouble(left);
                Double d2 = Double.parseDouble(right);

                if (op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=") ||
                        op.equals("==") || op.equals("!=")) {
                    ans = booleanOp(d1, d2, op);
                }

                if (op.equals("+") || op.equals("-") || op.equals("/") || op.equals("*") || op.equals("^")) {
                    ans = arithmeticOp(d1, d2, op, line_str);
                }

                map.put(varName, new Variable(varName, "double", ans));
            }
            return ans;
        }

        else {
            if (map.get(tree.getValue()).getType().equals("int")) {
                String line_str = "\"" + tree.getLineString() + "\"" + " (" + tree.getFileName() + ":" + tree.getLine_number() + ")";
                System.out.println("Syntax Error: Type mismatch: Expected Double got Integer, " + line_str);
                System.exit(-1);
            }
            return map.get(tree.getValue()).getValue();
        }
    }
}
