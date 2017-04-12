package CAS.EquationObjects;

import java.util.List;

/**
 * Created by jack on 1/15/2017.
 */
public class CustomFunction extends MathObject {
    public final String functionName;
    public CustomFunction(int args, boolean ordered, boolean associative, String functionName){
        super(args, ordered, associative, MathOperator.CUSTOM_FUNCTION);
        this.functionName = functionName;
    }
    public CustomFunction( String functionName, List<GenericExpression> args){
        super(args.size(), true, false, MathOperator.CUSTOM_FUNCTION);
        this.functionName = functionName;
    }
    @Override
    public String toString(){
        return functionName;
    }
}
