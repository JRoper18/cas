package CAS;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Ulysses Howard Smith on 4/6/2017.
 */
public class PatternMatchResult {
    public final boolean match;
    public final HashMap<String, Equation> variableValues;
    public final Equation equation;
    public final Equation pattern;
    public final LinkedList<Integer> errorPath;
    public PatternMatchResult(Equation eq, Equation pattern, boolean match, HashMap<String, Equation> patternValues, LinkedList<Integer> path){
        this.equation = eq;
        this.pattern = pattern;
        this.match = match;
        variableValues = patternValues;
        this.errorPath = path;
    }
    public void printError(){
        if(this.match){
            System.out.println("There was no error. ");
            return;
        }
        System.out.println("EXPECTED: " + this.expected());
        System.out.println("ACTUAL: " + this.actual());
    }
    public Equation expected(){
        if(this.match){
            return null;
        }
        Equation expected = new Equation(this.pattern.tree.getChildThroughPath(errorPath), 0);
        String possibleKey = expected.toString().substring(1, expected.toString().length());
        if(this.variableValues.containsKey(possibleKey)){
            return variableValues.get(possibleKey);
        }
        return expected;
    }
    public Equation actual(){
        if(this.match){
            return null;
        }
        return new Equation(this.equation.tree.getChildThroughPath(errorPath), 0);
    }
}
