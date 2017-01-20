package CAS.EquationObjects;

/**
 * Created by Ulysses Howard Smith on 1/20/2017.
 */
public class AbbriviationData {
    public MathObject op;
    public boolean isInfix;
    public AbbriviationData(MathObject op, boolean isInfix){
        this.op = op;
        this.isInfix = isInfix;
    }
}
