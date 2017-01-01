package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public class MathObjectNamed extends MathObject {
    public MathOperators operator;
    public MathObjectNamed(MathOperators op){
        super(op);
        this.operator = op;
    }
    public String getName(){
        return this.operator.toString();
    }
    @Override
    public String toString(){
        return this.getName();
    }
}
