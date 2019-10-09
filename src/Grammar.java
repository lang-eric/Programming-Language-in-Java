package src;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 *  A Grammar class that represents the grammar of Jott.
 *  The non-terminal is a parent node, and the child node of its next level
 *  is stored in an ArrayList.
 *
 *  For example, <stmt> --> <print> | <asmt> | <expr><end_statement>
 *              parent node : stmt
 *              child nodes: [[print], [asmt], [expr, end_statement]]
 */
public class Grammar {

    public List<List<NodeType>> programLR(){

        List<List<NodeType>> lst = new ArrayList<>();
        List<NodeType> list = new ArrayList<>();
        list.add(NodeType.STMT_LIST);
        list.add(NodeType.DDOLLAR);

        lst.add(list);

        return lst;
    }


    public List<List<NodeType>> stmtListLR(){
        List<List<NodeType>> lst = new ArrayList<>();
        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.STMT);
        lst1.add(NodeType.STMT_LIST);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.EMPTY);

        lst.add(lst1);
        lst.add(lst2);

        return lst;
    }


    public List<NodeType> startParenLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.START_PAREN);
        return lst;
    }

    public List<NodeType> endParenLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.END_PAREN);
        return lst;
    }


    public List<NodeType> endStmtLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.END_STMT);
        return lst;
    }

    public List<NodeType> echarLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.L_CHAR);
        lst.add(NodeType.U_CHAR);
        lst.add(NodeType.DIGIT);
        return lst;
    }

    public List<Character> lcharLR(){
        List<Character> lst = new ArrayList<>();
        char[] list = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
        's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for (int i = 0; i < list.length; i++) {
            lst.add(list[i]);
        }
        return lst;
    }

    public List<Character> ucharLR(){
        List<Character> lst = new ArrayList<>();
        char[] list = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        for (int i = 0; i < list.length; i++) {
            lst.add(list[i]);
        }
        return lst;
    }

    public List<Integer> digitLR(){
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lst.add(i);
        }
        return lst;
    }

    public List<NodeType> signLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.PLUS);
        lst.add(NodeType.MINUS);
        lst.add(NodeType.EMPTY);

        return lst;
    }

    public List<List<NodeType>> idLR(){
        List<List<NodeType>> lst = new ArrayList<>();
        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.L_CHAR);
        lst1.add(NodeType.CHAR);
        lst.add(lst1);

        return lst;
    }

    public List<List<NodeType>> stmtLR(){
        List<List<NodeType>> lst = new ArrayList<>();
        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.EXPR);
        lst1.add(NodeType.END_STMT);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.PRINT);

        List<NodeType> lst3 = new ArrayList<>();
        lst3.add(NodeType.ASMT);


        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);

        return lst;
    }

    public List<NodeType> exprLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.I_EXPR);
        lst.add(NodeType.D_EXPR);
        lst.add(NodeType.S_EXPR);
        lst.add(NodeType.ID);

        return lst;
    }

    public List<List<NodeType>> printLR(){
        List<List<NodeType>> lst = new ArrayList<>();
        List<NodeType> list = new ArrayList<>();
        list.add(NodeType.START_PAREN);
        list.add(NodeType.EXPR);
        list.add(NodeType.END_PAREN);
        list.add(NodeType.END_STMT);
        lst.add(list);

        return lst;
    }

    public List<List<NodeType>> asmtLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.D_EXPR);
        lst1.add(NodeType.END_STMT);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.I_EXPR);
        lst2.add(NodeType.END_STMT);

        List<NodeType> lst3 = new ArrayList<>();
        lst3.add(NodeType.S_EXPR);
        lst3.add(NodeType.END_STMT);


        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);

        return lst;
    }



    public List<NodeType> opLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.PLUS);
        lst.add(NodeType.MINUS);
        lst.add(NodeType.MULT);
        lst.add(NodeType.DIV);
        lst.add(NodeType.POWER);

        return lst;
    }

    public List<List<NodeType>> dblLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.SIGN);
        lst1.add(NodeType.DOT);
        lst1.add(NodeType.DIGIT);

        lst.add(lst1);

        return lst;
    }

    public List<List<NodeType>> dExprLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.ID);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.DBL);

        List<NodeType> lst3 = new ArrayList<>();
        lst3.add(NodeType.DBL);
        lst3.add(NodeType.OP);
        lst3.add(NodeType.DBL);

        List<NodeType> lst4 = new ArrayList<>();
        lst4.add(NodeType.DBL);
        lst4.add(NodeType.OP);
        lst4.add(NodeType.D_EXPR);

        List<NodeType> lst5 = new ArrayList<>();
        lst5.add(NodeType.D_EXPR);
        lst5.add(NodeType.OP);
        lst5.add(NodeType.DBL);

        List<NodeType> lst6 = new ArrayList<>();
        lst6.add(NodeType.D_EXPR);
        lst6.add(NodeType.OP);
        lst6.add(NodeType.D_EXPR);


        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);
        lst.add(lst4);
        lst.add(lst5);
        lst.add(lst6);

        return lst;
    }

    public List<List<NodeType>> intLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.SIGN);
        lst1.add(NodeType.DIGIT);

        lst.add(lst1);

        return lst;
    }

    public List<List<NodeType>> iExprLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.ID);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.INT);

        List<NodeType> lst3 = new ArrayList<>();
        lst3.add(NodeType.INT);
        lst3.add(NodeType.OP);
        lst3.add(NodeType.INT);

        List<NodeType> lst4 = new ArrayList<>();
        lst4.add(NodeType.INT);
        lst4.add(NodeType.OP);
        lst4.add(NodeType.I_EXPR);

        List<NodeType> lst5 = new ArrayList<>();
        lst5.add(NodeType.I_EXPR);
        lst5.add(NodeType.OP);
        lst5.add(NodeType.INT);

        List<NodeType> lst6 = new ArrayList<>();
        lst6.add(NodeType.I_EXPR);
        lst6.add(NodeType.OP);
        lst6.add(NodeType.I_EXPR);


        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);
        lst.add(lst4);
        lst.add(lst5);
        lst.add(lst6);

        return lst;
    }

    public List<List<NodeType>> sExprLR(){
        List<List<NodeType>> lst = new ArrayList<>();

        List<NodeType> lst1 = new ArrayList<>();
        lst1.add(NodeType.ID);

        List<NodeType> lst2 = new ArrayList<>();
        lst2.add(NodeType.STR_LITERAL);

        List<NodeType> lst3 = new ArrayList<>();
        lst3.add(NodeType.CONCAT);
        lst3.add(NodeType.START_PAREN);
        lst3.add(NodeType.S_EXPR);
        lst3.add(NodeType.COMMA);
        lst3.add(NodeType.S_EXPR);
        lst3.add(NodeType.END_PAREN);

        List<NodeType> lst4 = new ArrayList<>();
        lst4.add(NodeType.CHARAT);
        lst4.add(NodeType.START_PAREN);
        lst4.add(NodeType.S_EXPR);
        lst4.add(NodeType.COMMA);
        lst4.add(NodeType.I_EXPR);
        lst4.add(NodeType.END_PAREN);

        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);
        lst.add(lst4);

        return lst;
    }

    public List<NodeType> strLR(){
        List<NodeType> lst = new ArrayList<>();
        lst.add(NodeType.STR);
        return lst;
    }




}
