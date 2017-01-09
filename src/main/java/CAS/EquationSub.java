package CAS;

import CAS.EquationObjects.MathObjects.GenericExpression;
import CAS.EquationObjects.MathObjects.MathObject;
import com.rits.cloning.Cloner;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub {
    public final DirectOperation operation;
    public final Equation condition;
    public EquationSubProperties properties = new EquationSubProperties();
    public EquationSub(Equation before, Equation after){
        this.properties.assignedOperator = getProbableAssignedOperator(before);
        this.operation = ( eq -> {
            return this.substitute(before, after, eq);
        });
        this.condition = null;
    }
    public EquationSub(Equation before, Equation after, Equation conditions) {
        this.properties.assignedOperator = getProbableAssignedOperator(before);
        this.operation = (eq -> {
            return this.substitute(before, after, eq);
        });
        this.condition = conditions;
    }
    public EquationSub(DirectOperation operation){ //I'm hoping I'll only have to ever use this for adding, subtracting, division, and multiplication. Hoping.
        this.operation = operation;
        this.condition = null;
    }
    private MathObject getProbableAssignedOperator(Equation equation){
        MathObject probableOperator = null;
        if(equation.tree.data.getOperator().toString().contains("PATTERN")){ //Note to future self: Make a way to identify pattern objects from mathobjects.
            //Don't check pattern objects. Check the children for a mathobject.
            for(Tree<MathObject> child : equation.tree.getChildren()){
                MathObject possible = getProbableAssignedOperator(new Equation(child));
                if(probableOperator == null){
                    probableOperator = possible;
                }
                else{
                    if(!probableOperator.equals(possible)){
                        return null; //There's a difference. We can't tell.
                    }
                }
            }
            //Made it to the end with all of the operators the same? Probably that operator.
            return probableOperator;
        }
        else{
            return equation.tree.data;
        }
    }
    public Equation apply(Equation equation){
        Cloner cloner = new Cloner();
        Equation newEq = cloner.deepClone(equation);
        if(this.properties.assignedOperator != null){
            //It's specified. If it's associative, apply the operator for each section.
            if(this.properties.assignedOperator.isAssociative()){
                int operatorArgs = this.properties.assignedOperator.getArgs();
                while(newEq.tree.getNumberOfChildren() > operatorArgs){
                    Tree<MathObject> subEquationTree = new Tree<>(this.properties.assignedOperator);
                    //Add the children as the specified number of args
                    for(int j = 0; j < operatorArgs; j++){
                        subEquationTree.addChild(newEq.tree.getChild(j));
                    }
                    //So now we have a tree with a root operator and the right number of args. Simplify it, and then add it as the argument to the next simplification.
                    //EXAMPLE: 1 + 2 + 3 + 4 -> (((1+2)+3)+4) -> (3 + 3) + 4 -> 6 + 4 -> 10
                    //EXAMPLE 2: OR(F, F, T, F) -> OR ( OR (F, F) , T , F) -> OR (F, T, F ) -> OR(OR(F,T), F) -> OR(T, F) -> TRUE
                    Equation subEquation = new Equation(subEquationTree);
                    Equation temp = operation.operate(subEquation);
                    //Now replace
                    newEq.tree.setChild(0, temp.tree);
                    newEq.tree.removeChild(1);
                }
            }
        }
        return this.operation.operate(newEq);
    }
    private Equation substitute(Equation before, Equation after, Equation equation){
        PatternMatcher matcher = new PatternMatcher();
        if(matcher.patternMatch(equation, before)) {
            Equation newEquation = new Equation(after); //Quick clone
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            //Go through conditions
            if(this.condition != null){
                Cloner cloner = new Cloner();
                Equation temp = cloner.deepClone(condition);
                //Replace generics
                for(String var : values.keySet()){
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    temp.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                //No generics. Try evaluating it.
                Equation isGood = Simplifier.fullSimplify(condition);
                if(isGood.equals(new Equation("FALSE"))){
                    return equation; //Again, do nothing to the equation.
                }
            }
            for (String var : values.keySet()) {
                Tree<MathObject> substitution = values.get(var);
                GenericExpression genExToLookFor = new GenericExpression(var);
                newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
            }
            return newEquation;
        }
        return equation;
    }
    public Equation applyEverywhere(Equation equation){
        if(equation.tree.getNumberOfChildren() == 0){
            return this.apply(equation);
        }
        else{
            for(Tree<MathObject> childTree : equation.tree.getChildren()){
                childTree.replaceWith(this.applyEverywhere(new Equation(childTree)).tree);
            }
            return this.apply(equation);
        }
    }
}
