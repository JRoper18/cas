package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
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
    public void addStep(SubstitutionData step){
        Equation lastRoot = getResult();
        lastRoot.tree.getChildThroughPath(step.subPath).replaceWith(step.equation.tree);
        this.stepsTaken.add(new SubstitutionData(step.sub, Simplifier.simplifyWithMetaFunction(lastRoot, MathOperator.AUTOSIMPLIFY), step.subPath));
    }
    public void addSteps(List<SubstitutionData> steps){
        for(int i = 1; i<steps.size(); i++){
            this.addStep(steps.get(i));
        }
    }
    public void setPath(LinkedList<Integer> path){
        for(SubstitutionData step: stepsTaken){
            step.subPath.addAll(path);
        }
    }
    public Equation getInitial(){
        return stepsTaken.get(0).equation;
    }
    public Equation getResult(){
        return this.stepsTaken.get(this.stepsTaken.size() - 1).equation.clone();
    }
    public String getPrintableSteps(){
        StringBuilder build = new StringBuilder();
        for(int i = 0; i<this.stepsTaken.size(); i++){
            SubstitutionData step = this.stepsTaken.get(i);
            build.append(step.equation.toString());
            if(step.sub != null){
                build.append(" using sub " + step.sub);
                if(!step.subPath.isEmpty()){
                    build.append(" on sub-equation " + new Equation(stepsTaken.get(i - 1).equation.tree.getChildThroughPath(step.subPath), 0).toString());
                }
            }
            build.append("\n");
        }
        return build.toString();
    }
}
