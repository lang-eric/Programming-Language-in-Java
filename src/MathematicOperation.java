
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
    public String arithmeticOp(Object obj1, Object obj2, String op) {
        int ans = 0;
        double ans_double = 0;

        if (op.equals("+")) {
            if (obj1 instanceof Integer) {
                ans = (int) obj1 + (int) obj2;
                return String.valueOf(ans);
            }

            else {
                ans_double = (double) obj1 + (double) obj2;
                return String.valueOf(ans_double);
            }
        }

        else if (op.equals("-")){
            if (obj1 instanceof Integer) {
                ans = (int) obj1 - (int) obj2;
                return String.valueOf(ans);
            }
            else {
                ans_double = (double) obj1 - (double) obj2;
                return String.valueOf(ans_double);
            }
        }

        else if (op.equals("*")) {
            if (obj1 instanceof Integer) {
                ans = (int) obj1 * (int) obj2;
                return String.valueOf(ans);
            }
            else {
                ans_double = (double) obj1 * (double) obj2;
                return String.valueOf(ans_double);
            }
        }

        else if (op.equals("^")) {
            if (obj1 instanceof Integer){
                ans_double = Math.pow((int) obj1, (int) obj2);
                ans = (int) Math.round(ans_double);
                return String.valueOf(ans);
            }
            else {
                ans_double = Math.pow((double) obj1, (double) obj2);
            }
            return String.valueOf(ans_double);
        }

        else {

            if (obj1 instanceof Integer) {
                try {
                    ans_double = (int) obj1 / (int) obj2;
                    ans = (int) Math.round(ans_double);
                    return String.valueOf(ans);

                }
                catch (Exception e){
                    System.out.println("Runtime Error: Cannot divide by zero!");
                    System.exit(-1);
                }
                return String.valueOf(ans_double);
            }

            else {
                try {
                    ans_double = (double) obj1 / (double) obj2;

                }
                catch (Exception e){
                    System.out.println("zero can not be divided...");
                }
                return String.valueOf(ans_double);
            }
        }
//        if (obj1 instanceof Integer)
//            return String.valueOf(ans);
//        return String.valueOf(ans_double);

    }



}
