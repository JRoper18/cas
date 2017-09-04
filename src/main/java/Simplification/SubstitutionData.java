package Simplification;

import CAS.Equation;
import Substitution.EquationSub;

import java.util.LinkedList;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public class SubstitutionData {
    public final Equation equation;
    public final EquationSub sub;
    public final LinkedList<Integer> subPath;
    public SubstitutionData(EquationSub sub, Equation eq){
        this.sub = sub;
        this.equation = eq;
        this.subPath = new LinkedList<>();
    }
    public SubstitutionData(EquationSub sub, Equation eq, LinkedList<Integer> path){
        this.sub = sub;
        this.equation = eq;
        this.subPath = path;
    }
    @Override
    public boolean equals(Object n){
        if(n instanceof SubstitutionData){
            return ((SubstitutionData) n).equation.equals(this.equation) && ((SubstitutionData) n).sub.equals(this.sub) && ((SubstitutionData) n).subPath.equals(this.subPath);
        }
        return false;
    }

    @Override
    public String toString(){
        return sub + " " + equation;
    }
}
