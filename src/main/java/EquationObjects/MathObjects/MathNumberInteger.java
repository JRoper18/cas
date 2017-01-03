package EquationObjects.MathObjects;

import org.bidouille.jops.Operator;

import java.math.BigInteger;

/**
 * Created by jack on 12/30/2016.
 */
public class MathNumberInteger extends MathNumberRational{
    public BigInteger num;
    public MathNumberInteger(int num){
        super(num);
        this.num = BigInteger.valueOf(num);
    }
    public MathNumberInteger(int num, boolean endTheInfiniteLoopPlz){
        super();
        this.num = BigInteger.valueOf(num);
    }
    public MathNumberInteger(BigInteger num){
        super(num.intValue());
        this.num = num;
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MathNumberInteger)){
            return false;
        }
        MathNumberInteger num = (MathNumberInteger) obj;
        return (num.num.compareTo(this.num) == 0);
    }

    @Override
    public String toString(){
        return this.num.toString();
    }

    @Operator( "+" )
    public static MathNumberInteger add(MathNumberInteger one, MathNumberInteger two){
        return new MathNumberInteger(one.num.add(two.num));
    }

    @Operator( "*" )
    public static MathNumberInteger mul(MathNumberInteger one, MathNumberInteger two){
        return new MathNumberInteger(one.num.multiply(two.num));
    }
}
