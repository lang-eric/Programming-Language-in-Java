package src;

import java.util.ArrayList;
import java.util.List;

public class Grammar {
    public List<List<Type>> programLR(){

        List<List<Type>> lst = new ArrayList<>();
        List<Type> list = new ArrayList<>();
        list.add(Type.stmt_list);
        list.add(Type.ddollar);

        lst.add(list);

        return lst;
    }


    public List<List<Type>> stmtListLR(){
        List<List<Type>> lst = new ArrayList<>();
        List<Type> lst1 = new ArrayList<>();
        lst1.add(Type.stmt);
        lst1.add(Type.stmt_list);

        List<Type> lst2 = new ArrayList<>();
        lst2.add(Type.empty);

        lst.add(lst1);
        lst.add(lst2);

        return lst;
    }


    public List<Type> startParenLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.start_paren);
        return lst;
    }

    public List<Type> endParenLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.end_paren);
        return lst;
    }


    public List<Type> endStmtLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.end_stmt);
        return lst;
    }

    public List<Type> echarLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.l_char);
        lst.add(Type.u_char);
        lst.add(Type.digit);
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

    public List<Type> signLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.plus);
        lst.add(Type.minus);
        lst.add(Type.empty);

        return lst;
    }

    public List<List<Type>> idLR(){
        List<List<Type>> lst = new ArrayList<>();
        List<Type> lst1 = new ArrayList<>();
        lst1.add(Type.l_char);
        lst1.add(Type.CHAR);
        lst.add(lst1);

        return lst;
    }

    public List<List<Type>> stmtLR(){
        List<List<Type>> lst = new ArrayList<>();
        List<Type> lst1 = new ArrayList<>();
        lst1.add(Type.expr);
        lst1.add(Type.end_stmt);

        List<Type> lst2 = new ArrayList<>();
        lst2.add(Type.print);

        List<Type> lst3 = new ArrayList<>();
        lst3.add(Type.asmt);

        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);

        return lst;
    }

    public List<Type> exprLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.i_expr);
        lst.add(Type.d_expr);
        lst.add(Type.s_expr);
        lst.add(Type.id);

        return lst;
    }

    public List<List<Type>> printLR(){
        List<List<Type>> lst = new ArrayList<>();
        List<Type> list = new ArrayList<>();
        list.add(Type.start_paren);
        list.add(Type.expr);
        list.add(Type.end_paren);
        list.add(Type.end_stmt);
        lst.add(list);

        return lst;
    }

    public List<List<Type>> asmtLR(){
        List<List<Type>> lst = new ArrayList<>();

        List<Type> lst1 = new ArrayList<>();
        lst1.add(Type.d_expr);
        lst1.add(Type.end_stmt);

        List<Type> lst2 = new ArrayList<>();
        lst2.add(Type.i_expr);
        lst2.add(Type.end_stmt);

        List<Type> lst3 = new ArrayList<>();
        lst3.add(Type.s_expr);
        lst3.add(Type.end_stmt);

        lst.add(lst1);
        lst.add(lst2);
        lst.add(lst3);

        return lst;
    }



    public List<Type> opLR(){
        List<Type> lst = new ArrayList<>();
        lst.add(Type.plus);
        lst.add(Type.minus);
        lst.add(Type.mult);
        lst.add(Type.div);
        lst.add(Type.power);

        return lst;
    }


}
