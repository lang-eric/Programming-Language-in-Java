
public class Variable {
    //name of variable.
    private String var_name;

    //contents of variable.
    private String type;
    private String value;

    /*
    Variable class constructor takes variable's name, than variable's contents (type, and value).
     */

    public Variable(String var_name, String type, String value ){
        this.var_name = var_name;
        this.type = type;
        this.value = value;
    }

    public String getType(){
        return type;
    }
    /** for operations, type casting might need to be done during output/parsing).
     */
    public String getValue(){
        return value;
    }

    public String getVarName() {
        return var_name;
    }
}
