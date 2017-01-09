package CAS;

import CAS.EquationObjects.MathObjects.MathObject;

import java.io.Serializable;

/**
 * Created by jack on 1/7/2017.
 */
public class EquationSubProperties implements Serializable {
    public MathObject assignedOperator; //If this substitution works specifically on a single operator.
    public EquationSubProperties(){

    }
}
