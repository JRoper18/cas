package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.Tree;
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
    public Equation initial;
    public Equation result;
    public List<EquationSub> subsUsed;
    public List<Equation> changes;
    public List<LinkedList<Integer>> paths;
    public SimplifierResult(Equation initial){
        this.initial = initial.clone();
        this.result = initial.clone();
        this.subsUsed = new ArrayList<>();
        this.changes = new ArrayList<>();
        this.paths = new ArrayList<>();
    }
    public SimplifierResult(Equation initial, Equation eq, List<EquationSub> subs, List<Equation> changes){
        this.initial = initial.clone();
        this.result = eq.clone();
        this.subsUsed = subs;
        this.changes = changes;
        this.paths = new ArrayList<>();
    }
    public String steps(){
        StringBuilder build = new StringBuilder();
        build.append(initial.toString());
        build.append("\n");
        for(int i = 0; i<this.changes.size(); i++){
            Equation step = this.changes.get(i);
            EquationSub sub = this.subsUsed.get(i);
            build.append(step.toString());
            build.append(" using sub: " + sub.toString());
            build.append("\n");
        }
        build.append(result.toString());
        return build.toString();
    }
    public void combineSub(SimplifierResult combine){

    }
    /**
     * Combines two seperate simplifications, provided that result is a comtinuation of this simplification.
     * @param result The next steps in our simplification that we want to combine.
     */
    public void combine(SimplifierResult combine){
        if(!combine.initial.equals(this.result)){
            throw new UncheckedIOException(new IOException("The appended result must be a continuation of this result. We need " + this.result + " but got " + combine.initial + "."));
        }
        this.changes.addAll(combine.changes);
        this.subsUsed.addAll(combine.subsUsed);
        this.result = combine.result;

    }
}
