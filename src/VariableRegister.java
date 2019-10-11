import java.util.HashMap;

public class VariableRegister {
    private static HashMap<String, Variable> variableRegister = new HashMap<String, Variable>();

    public static HashMap<String, Variable> getVariableRegister(){
        return variableRegister;
    }
    public static Variable getVariable(String variable_name){
        return variableRegister.get(variable_name);
    }
    public static void addVariable(String variable_name, String type, String o){
        getVariableRegister().put(variable_name, new Variable(variable_name, type, o));
    }



}
