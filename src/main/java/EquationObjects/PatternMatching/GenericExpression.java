package EquationObjects.PatternMatching;

import EquationObjects.EquationObject;

/**
 * Created by Ulysses Howard Smith on 10/26/2016.
 */
public class GenericExpression extends EquationObject {
    public GenericType type;
    public String tag;
    public GenericExpression(){
        super(2);
        this.tag = null;
        this.type = null;
    }
    public GenericExpression(GenericType type, String tag){
        super(2);
        this.tag = tag;
        this.type = type;
    }
    public GenericExpression(GenericType type){
        super(2);
        this.tag = null;
        this.type = type;
    }
    public GenericExpression(String tag){
        super(2);
        this.tag = tag;
        this.type = null;
    }
}
