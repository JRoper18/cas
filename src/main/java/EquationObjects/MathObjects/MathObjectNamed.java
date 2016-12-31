package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public class MathObjectNamed extends MathObject {
    public String name;
    public MathObjectNamed(MathOperators op){
        super(op);
        this.name = op.toString();
    }
}
