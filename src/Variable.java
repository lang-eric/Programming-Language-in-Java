import java.lang.reflect.Type;

public class Variable {
    //name of variable.
    private String var_name;

    //contents of variable.
    private Class<?> type;
    private Object value;

    /*
    Variable class constructor takes variable's name, than variable's contents (type, and value).
     */

    public Variable(String var_name, Class<?> type, Object value ){
        this.var_name = var_name;
        this.type = type;
        this.value = value;
    }

    public Class<?> getType(){
        return type;
    }

    public <T> T getValue(){
        return (T) type.cast(value);
    }

    public String getVarName() {
        return var_name;
    }
}
