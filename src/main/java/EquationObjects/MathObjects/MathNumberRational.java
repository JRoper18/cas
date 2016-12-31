package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public class MathNumberRational extends MathNumberReal {
    public MathNumberInteger numerator;
    public MathNumberInteger denominator;
    public MathNumberRational(MathNumberInteger numerator, MathNumberInteger denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }
    public MathNumberRational(int numerator, int denominator){
        this.numerator = new MathNumberInteger(numerator, true);
        this.denominator = new MathNumberInteger(denominator, true);
    }
    public MathNumberRational(int numerator){
        this.numerator = new MathNumberInteger(numerator, true);
        this.denominator = new MathNumberInteger(1, true);
    }
    protected MathNumberRational(){

    }
    @Override
    public boolean equals(Object object){
        if(!(object instanceof MathNumberRational)){
            return false;
        }
        MathNumberRational num = (MathNumberRational) object;
        return (num.numerator.equals(this.numerator) && num.denominator.equals(this.denominator));

    }
}
