package CAS;

import CAS.EquationObjects.MathObject;
import com.rits.cloning.Cloner;

import java.io.Serializable;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub implements Serializable {
    public final DirectOperation operation;
    public MathObject rootOperator;
    public EquationSub(DirectOperation operation){
        this.operation = operation;
        this.rootOperator = null;
    }
    public EquationSub(DirectOperation operation, MathObject operator){ //I'm hoping I'll only have to ever use this for adding, subtracting, division, and multiplication. Hoping.
        this.operation = operation;
        this.rootOperator = operator;
    }
    public Equation apply(Equation equation){
        Cloner cloner = new Cloner();
        Equation newEq = cloner.deepClone(equation);
        if(this.rootOperator != null){
            //It's specified. If it's associative, apply the operator for each section.
            if(this.rootOperator.isAssociative()){
                int operatorArgs = this.rootOperator.getArgs();
                while(newEq.tree.getNumberOfChildren() > operatorArgs){
                    Tree<MathObject> subEquationTree = new Tree<>(this.rootOperator);
                    //Add the children as the specified number of args
                    for(int j = 0; j < operatorArgs; j++){
                        subEquationTree.addChild(newEq.tree.getChild(j));
                    }
                    //So now we have a tree with a root operator and the right number of args. Simplify it, and then add it as the argument to the next simplification.
                    //EXAMPLE: 1 + 2 + 3 + 4 -> (((1+2)+3)+4) -> (3 + 3) + 4 -> 6 + 4 -> 10
                    //EXAMPLE 2: OR(F, F, T, F) -> OR ( OR (F, F) , T , F) -> OR (F, T, F ) -> OR(OR(F,T), F) -> OR(T, F) -> TRUE
                    Equation subEquation = new Equation(subEquationTree);
                    Equation temp = Simplifier.simplify(subEquation);
                    //Now replace
                    newEq.tree.setChild(0, temp.tree);
                    newEq.tree.removeChild(1);
                }
            }
        }
        return this.operation.operate(newEq);
    }
    public Equation applyEverywhere(Equation equation){
        if(!equation.tree.hasChildren()){
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
