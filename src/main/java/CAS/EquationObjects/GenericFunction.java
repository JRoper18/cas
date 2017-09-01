package CAS.EquationObjects;

/**
 * Created by Jack Roper on 8/24/2017.
 */
public class GenericFunction extends MathObject{
    public GenericFunction(String name){
        super(MathOperator.GENERIC_FUNCTION);
        this.name = name;
    }

    @Override
    public String toString(){
        return "_" + this.name + "_GENERICFUNCTION";
    }
}
