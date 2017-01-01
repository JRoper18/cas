package EquationObjects.MathObjects;

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
}
