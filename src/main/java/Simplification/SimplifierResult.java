package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import Util.Tree;
import Substitution.EquationSub;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jack Roper on 4/3/2017.
 */
public class SimplifierResult {
    /**
     * This class holds results from a simplification.
     * initial holds the starting equation
     * result holds the final, simplified equation
     * subsUsed holds which subs were used.
     */
    public LinkedList<SubstitutionData> stepsTaken;
    public SimplifierResult(Equation initial){
        this.stepsTaken = new LinkedList<>();
        stepsTaken.add(new SubstitutionData(null, initial));
    }
    public SimplifierResult(LinkedList<SubstitutionData> steps){
        this.stepsTaken = steps;
    }
    public Equation getInitial(){
        return stepsTaken.get(0).equation;
    }
    public Equation getResult(){
        return stepsTaken.get(stepsTaken.size()-1).equation;
    }
    public String getPrintableSteps(){
        StringBuilder build = new StringBuilder();
        for(int i = 0; i<this.stepsTaken.size(); i++){
            SubstitutionData step = this.stepsTaken.get(i);
            build.append(step.toString());
            build.append("\n");
        }
        return build.toString();
    }
}
