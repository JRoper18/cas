package CAS.EquationObjects;

/**
 * Created by Jack Roper on 8/24/2017.
 */
public class GenericFunction extends MathObject{
    public GenericFunction(String name){
        super(MathOperator.GENERIC_FUNCTION);
        this.name = "_" + name + "_GENERICFUNCTION";
    }
}
