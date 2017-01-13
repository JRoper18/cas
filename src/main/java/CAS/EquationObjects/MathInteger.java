package CAS.EquationObjects;

import java.math.BigInteger;

/**
 * Created by jack on 12/30/2016.
 */
public class MathInteger extends MathObject {
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

    public MathInteger add(MathInteger other){
        return new MathInteger(this.num.add(other.num));
    }

    public MathInteger mul(MathInteger other){
        return new MathInteger(this.num.multiply(other.num));
    }

    public MathInteger sub(MathInteger other){
        return new MathInteger(this.num.subtract(other.num));
    }

    public MathInteger div(MathInteger other){ //Assume that they are just being simplified, and you wont get inputs like 5/2.
        return new MathInteger(this.num.divide(other.num));
    }
}
