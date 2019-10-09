import java.util.HashMap;

public class VariableRegister {
    private static HashMap<String, Variable> variableRegister = new HashMap<String, Variable>();

    public static HashMap<String, Variable> getVariableRegister(){
        return variableRegister;
    }
    public static Variable getVariable(String variable_name){
        return variableRegister.get(variable_name);
    }
    public static void addVariable(String variable_name, Class<?> type, Object o){
        getVariableRegister().put(variable_name, new Variable(variable_name, type, o));
    }
    public static void main(String[] args) {
        //variable name
        String var_name = "name";
        //type
        Class<?> type = String.class;
        //object
        String name = "Eric";

        VariableRegister.addVariable(var_name, String.class,"Eric");
        System.out.println(type.cast(VariableRegister.getVariable(var_name).getValue()));
    }


}
