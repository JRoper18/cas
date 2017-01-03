package EquationObjects.MathObjects;

/**
 * Created by Ulysses Howard Smith on 10/26/2016.
 */
public class GenericExpression extends MathObject {
    public String tag;
    public GenericExpression(){
        super(MathSymbol.EXPRESSION);
        this.tag = null;
    }
    public GenericExpression(String tag){
        super(MathSymbol.EXPRESSION);
        this.tag = tag;
    }

    public boolean hasTag(){
        return this.tag == null;
    }
}
