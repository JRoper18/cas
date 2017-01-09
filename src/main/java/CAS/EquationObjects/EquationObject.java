package CAS.EquationObjects;

/**
 * Created by jack on 12/30/2016.
 */
public abstract class EquationObject {
    protected int args;
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
