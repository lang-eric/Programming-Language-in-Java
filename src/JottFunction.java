// The function object will hold everything that functions will hold


import java.util.ArrayList;

public class JottFunction {
    public static class Parameter{
        String name;
        NodeType TYPE;
        Parameter(NodeType TYPE, String name){
            this.TYPE=TYPE;
            this.name=name;
        }
        public String getName() {
            return name;
        }

        public NodeType getTYPE() {
            return TYPE;
        }

    }

    String name;
    NodeType return_type;
    ArrayList<Parameter> parameters=new ArrayList<>();
    ParseTreeNode parent_node;
    JottFunction(String name, NodeType return_type,ArrayList<Parameter> parameters, ParseTreeNode body){
        this.name=name;
        this.return_type=return_type;
        this.parameters=parameters;
        this.parent_node=body;
    }

    public String getName() {
        return name;
    }

    public NodeType getReturn_type() {
        return return_type;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public ParseTreeNode getBody() {
        return parent_node;
    }
    
}
