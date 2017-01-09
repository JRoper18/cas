package CAS.EquationObjects;

import CAS.AbstractSerializationObject;

import java.io.Serializable;

/**
 * Created by jack on 12/30/2016.
 */
public abstract class EquationObject extends AbstractSerializationObject implements Serializable {
    public int args;
    public EquationObject(int args){
        this.args = args;
    }
    public int getArgs(){
        return this.args;
    }
    @Override
    public boolean equals(Object obj){
        if(obj.getClass() == EquationObject.class){
            return this.args == ((EquationObject) obj).getArgs();
        }
        return false;
    }
}
