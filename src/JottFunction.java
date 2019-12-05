// The function object will hold everything that functions will hold


import java.util.ArrayList;

public class JottFunction {
    public static class Parameter{
        String name;
        type TYPE;
        Parameter(type TYPE, String name){
            this.TYPE=TYPE;
            this.name=name;
        }

        public String getName() {
            return name;
        }

        public type getTYPE() {
            return TYPE;
        }

    }
    enum type{
        STRING, INTEGER, DOUBLE, VOID;
    }
    String name;
    type return_type;
    ArrayList<Parameter> parameters=new ArrayList<>();
    ParseTreeNode parent_node;
    ArrayList<ParseTreeNode> nodes=new ArrayList<>();

    JottFunction(String name, type return_type,ArrayList<Parameter> parameters, ParseTreeNode parent_node){
        this.name=name;
        this.return_type=return_type;
        this.parameters=parameters;
        this.parent_node=parent_node;
    }

    public String getName() {
        return name;
    }

    public type getReturn_type() {
        return return_type;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public ParseTreeNode getParent_node() {
        return parent_node;
    }

    public void add_Parameter(type what_type, String name){
        this.parameters.add(new Parameter(what_type,name));
    }
    
}
