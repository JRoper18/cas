package Simplification;

import CAS.Equation;
import Substitution.EquationSub;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public class SubstitutionData {
    public final Equation equation;
    public final EquationSub sub;
    public SubstitutionData(EquationSub sub, Equation eq){
        this.sub = sub;
        this.equation = eq;
    }
    @Override
    public boolean equals(Object n){
        if(n instanceof SubstitutionData){
            return ((SubstitutionData) n).equation.equals(this.equation) && ((SubstitutionData) n).sub.equals(this.sub);
        }
        return false;
    }

    @Override
    public String toString(){
        return sub + " " + equation;
    }
}
