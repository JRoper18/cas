package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public class MathNumberComplex extends MathObject {
    MathNumberReal real;
    MathNumberReal imaginary;
    public MathNumberComplex(MathNumberReal realPart, MathNumberReal imaginaryPart){
        super(0, false, false);
        this.real = realPart;
        this.imaginary = imaginaryPart;
    }
}
