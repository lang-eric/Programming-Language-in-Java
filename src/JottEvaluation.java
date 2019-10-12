import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JottEvaluation {

    private static VariableRegister variableRegister = new VariableRegister();
    private static MathematicOperation mathematicOperation = new MathematicOperation();
    private static List<String> outputs = new ArrayList<>();
    private static HashMap<String, Variable> map = new HashMap<>();
    public static VariableRegister getVariableRegister(){
        return variableRegister;
    }

    private static String varName = "";

    public static List<String> JottEvaluation(ParseTreeNode tree) {
        if (tree.getAllChildren().size() == 0) {
            return null;
        }
        stmtListEval(tree.getAllChildren().get(0));

        return outputs;
    }

    public static void stmtEval(ParseTreeNode tree){
        List<ParseTreeNode> children = tree.getAllChildren();
        if (children.get(0).getNodeType().equals(NodeType.ASMT)) {
            varName = children.get(0).getAllChildren().get(1).getValue();
            ASMTEval(children.get(0));
            varName = "";
        }

        else if (children.get(0).getNodeType().equals(NodeType.PRINT)) {
            varName = "";
            printEval(children.get(0));
        }

        else {
            exprEval(children.get(0));
        }
    }

    public static void stmtListEval(ParseTreeNode tree){
        List<ParseTreeNode> children = tree.getAllChildren();

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
        return null;
    }

    public static String intEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String ans = "";
        if (tree.getNodeType().equals(NodeType.INT) || tree.getNodeType().equals(NodeType.OP)) {
            return tree.getValue();
        }

        else if (tree.getNodeType().equals(NodeType.I_EXPR)) {
            if (nums != 0) {
                if (nums == 1) {
                    ans = children.get(0).getValue();
                    map.put(varName, new Variable(varName, "int", ans));
                    return children.get(0).getValue();
                }

                String left = intEval(children.get(0));
                String op = intEval(children.get(1));
                String right = intEval(children.get(2));

                int d1 = Integer.parseInt(left);
                int d2 = Integer.parseInt(right);
                ans = mathematicOperation.arithmeticOp(d1, d2, op);
                map.put(varName, new Variable(varName, "int", ans));
            }
            //TODO:ERROR
            return ans;
        }

        else {
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

    public static void exprEval(ParseTreeNode tree) {

    }

    public static String doubleEval(ParseTreeNode tree) {
        int nums = tree.getAllChildren().size();
        List<ParseTreeNode> children = tree.getAllChildren();
        String ans = "";
        if (tree.getNodeType().equals(NodeType.DBL) || tree.getNodeType().equals(NodeType.OP)) {
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

                Double d1 = Double.parseDouble(left);
                Double d2 = Double.parseDouble(right);
                ans = mathematicOperation.arithmeticOp(d1, d2, op);
                map.put(varName, new Variable(varName, "double", ans));
            }
            //TODO:ERROR
            return ans;
        }

        else {
            return map.get(tree.getValue()).getValue();
        }
    }
}
