package CAS.EquationObjects;

import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.HashMap;

/**
 * Created by jack on 12/30/2016.
 */
public class MathObject implements Serializable {
    private int args;
    private boolean ordered;
    private boolean associative;
    protected MathOperator operator;
    protected String name;
    private static HashMap<String, CustomFunction> customFunctions = new HashMap<>();
    public MathObject(String customFunctionName){
        CustomFunction func = customFunctions.get(customFunctionName);
        this.args = func.getArgs();
        this.ordered = func.isOrdered();
        this.associative = func.isAssociative();
        this.name = func.functionName;
    }
    public MathObject(MathOperator op) {
        this.args = op.getArguments();
        this.ordered = op.isOrdered();
        this.associative = op.isAssociative();
        this.operator = op;
        this.name = operator.toString();
    }
    public MathObject(int args, boolean ordered, boolean associative, MathOperator operator){
        this.args = args;
        this.ordered = ordered;
        this.associative = associative;
        this.operator = operator;
        this.name = operator.toString();
    }
    public int getArgs(){
        return this.args;
    }
    public boolean isOrdered(){
        return this.ordered;
    }
    public boolean isAssociative(){
        return this.associative;
    }
    public MathOperator getOperator(){
        return this.operator;
    }
    public String getName(){
        return this.name;
    }

    public static boolean doesCustomFunctionExist(String functionName){
        return customFunctions.containsKey(functionName);
    }
    public static void addCustomFunction(CustomFunction func){
        customFunctions.put(func.functionName, func);
    }
    @Override
    public String toString(){
        return this.getName();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof MathObject){
            return this.args == ((MathObject) obj).getArgs() && this.ordered == ((MathObject) obj).isOrdered() && this.associative == ((MathObject) obj).isAssociative() && this.operator == ((MathObject) obj).getOperator();
        }
        return false;
    }
}
