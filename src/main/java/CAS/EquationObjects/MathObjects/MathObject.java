package CAS.EquationObjects.MathObjects;

import CAS.EquationObjects.EquationObject;

/**
 * Created by jack on 12/30/2016.
 */
public class MathObject extends EquationObject {
    private boolean ordered;
    private boolean associative;
    private MathSymbol operator;
    public MathObject(MathSymbol operator){
        super(operator.getArguments());
        this.ordered = operator.isOrdered();
        this.associative = operator.isAssociative();
        this.operator = operator;
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
    public MathSymbol getOperator(){
        return this.operator;
    }
    public String getName(){
        return this.operator.toString();
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
