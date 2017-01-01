package EquationObjects.MathObjects;

import EquationObjects.EquationObject;

/**
 * Created by jack on 12/30/2016.
 */
public abstract class MathObject extends EquationObject {
    private int args;
    private boolean ordered;
    private boolean associative;
    public MathObject(int args, boolean ordered, boolean associative){
        this.args = args;
        this.ordered = ordered;
        this.associative = associative;
    }
    public MathObject(MathOperators operator){
        this.args = operator.getArguments();
        this.ordered = operator.isOrdered();
        this.associative = operator.isAssociative();
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
}
