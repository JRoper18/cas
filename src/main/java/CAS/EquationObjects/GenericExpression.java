package CAS.EquationObjects;

/**
 * Created by Ulysses Howard Smith on 10/26/2016.
 */
public class GenericExpression extends MathObject {
    public String tag;
    public boolean named;
    public GenericExpression(){
        super(MathOperator.EXPRESSION);
        this.tag = null;
    }
    public GenericExpression(String tag){
        super(MathOperator.EXPRESSION);
        this.tag = tag;
    }

    public boolean hasTag(){
        return this.tag != null;
    }

    @Override
    public boolean equals(Object n){
        if(n instanceof GenericExpression && n != null){
            if(((GenericExpression) n).tag == null){
                return this.tag == null;
            }
            return ((GenericExpression) n).tag.equals(this.tag);
        }
        return false;
    }

    @Override
    public String toString(){
        return "_" + tag;
    }
}
