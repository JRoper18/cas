package EquationObjects.MathObjects;

import org.bidouille.jops.Operator;

import java.math.BigInteger;

/**
 * Created by jack on 12/30/2016.
 */
public class MathInteger extends MathObject{
    public BigInteger num;
    public MathInteger(int num){
        super(MathSymbol.NUMBER);
        this.num = BigInteger.valueOf(num);
    }
    public MathInteger(BigInteger num){
        super(MathSymbol.NUMBER);
        this.num = num;
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MathInteger)){
            return false;
        }
        MathInteger num = (MathInteger) obj;
        return (num.num.compareTo(this.num) == 0);
    }

    @Override
    public String toString(){
        return this.num.toString();
    }

    @Operator( "+" )
    public static MathInteger add(MathInteger one, MathInteger two){
        return new MathInteger(one.num.add(two.num));
    }

    @Operator( "*" )
    public static MathInteger mul(MathInteger one, MathInteger two){
        return new MathInteger(one.num.multiply(two.num));
    }
}
