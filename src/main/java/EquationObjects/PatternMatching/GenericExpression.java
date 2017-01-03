package EquationObjects.PatternMatching;

import EquationObjects.EquationObject;

/**
 * Created by Ulysses Howard Smith on 10/26/2016.
 */
public class GenericExpression extends EquationObject {
    public String tag;
    public GenericExpression(){
        super(0);
        this.tag = null;
    }
    public GenericExpression(String tag){
        super(0);
        this.tag = tag;
    }

    public boolean hasTag(){
        return this.tag == null;
    }
}
