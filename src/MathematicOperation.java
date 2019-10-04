package src;

public class MathematicOperation {
    /**
     * Function: arithmeticOp
     * Description: a function to deal with the Arithmetic calculation
     * Pre-condition: the inputs obj1 and obj2 must be the same type which is either
     *                Integer or Double
     * @param obj1 -- the input number 1
     * @param obj2 -- the input number 2
     * @param op -- the operator which is stored as a string
     * @return -- the result of the calculation in either int or double type
     */
    public Object arithmeticOp(Object obj1, Object obj2, String op) {
        int ans = 0;
        double ans_double = 0;

        if (op.equals("+")) {
            if (obj1 instanceof Integer)
                ans = (int) obj1 + (int) obj2;
            else
                ans_double = (double) obj1 + (double)obj2;
        }

        else if (op.equals("-")){
            if (obj1 instanceof Integer)
                ans = (int) obj1 - (int) obj2;
            else
                ans_double = (double) obj1 - (double)obj2;
        }

        else if (op.equals("*")) {
            if (obj1 instanceof Integer)
                ans = (int) obj1 + (int) obj2;
            else
                ans_double = (double) obj1 + (double) obj2;
        }

        else {

            if (obj1 instanceof Integer) {
                try {
                    ans_double = (int) obj1 / (int) obj2;

                }
                catch (Exception e){
                    System.out.println("zero can not be divided...");
                }
            }

            else {
                try {
                    ans_double = (double) obj1 / (double) obj2;

                }
                catch (Exception e){
                    System.out.println("zero can not be divided...");
                }
            }
        }
        if (obj1 instanceof Integer)
            return ans;
        return ans_double;

    }



}
