package CAS.EquationObjects;


/**
 * Created by jack on 1/3/2017.
 */
public class RationalTempInfoHolder{
    public MathInteger numer;
    public MathInteger denom;
    public RationalTempInfoHolder(MathInteger numer, MathInteger denom){
        this.numer = numer;
        this.denom = denom;
    }

    @Override
    public boolean equals(Object n){
        if(n instanceof RationalTempInfoHolder){
            return this.numer.equals(((RationalTempInfoHolder) n).numer) && this.denom.equals(((RationalTempInfoHolder) n).denom);
        }
        return false;
    }
}
