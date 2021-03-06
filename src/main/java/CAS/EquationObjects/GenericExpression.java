package CAS.EquationObjects;

import Identification.IdentificationType;

/**
 * Created by Ulysses Howard Smith on 10/26/2016.
 */
public class GenericExpression extends MathObject {
    public String tag;
    public boolean named;
    public IdentificationType type;
    public GenericExpression(){
        super(MathOperator.EXPRESSION);
        this.tag = null;
        this.type = IdentificationType.EXPRESSION;
    }
    public GenericExpression(String tag, boolean named, IdentificationType type){
        super(MathOperator.EXPRESSION);
        this.tag = tag;
        this.named = named;
        this.type = type;
    }
    public GenericExpression(String tag, boolean named){
        super(MathOperator.EXPRESSION);
        this.tag = tag;
        this.named = named;
        this.type = IdentificationType.VARIABLE;
    }
    public GenericExpression(String tag){
        super(MathOperator.EXPRESSION);
        this.tag = tag;
        this.named = false;
        this.type = IdentificationType.VARIABLE;
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
        return "_" + ((this.named)? "_" : "") + tag + ((this.type == IdentificationType.VARIABLE) ? "" : "_" + this.type.toString());
    }
}
