package Simplification.Methods;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import Identification.Identifier;
import Simplification.SimplifierResult;
import Simplification.SimplifierStrategy;
import Simplification.SimplifyObjectiveNotDoneException;
import Util.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jack Roper on 9/3/2017.
 */
public class OrderEquationSimplify extends SimplifierStrategy {
    public boolean isSimplifyDone(Equation begin, Equation current){
        try{
            return new OrderEquationSimplify().getResult(begin).equals(current);
        } catch (SimplifyObjectiveNotDoneException ex){
            return false;
        }
    }
    public SimplifierResult simplify(Equation equation){
        Tree<MathObject> eqTree = equation.tree.clone();
        List<Equation> newChildren = equation.getOperands();
        if(eqTree.hasChildren()){
            newChildren.clear();
            for(int i = 0; i<eqTree.getNumberOfChildren(); i++){
                Equation child = equation.getSubEquation(i);
                newChildren.add(new OrderEquationSimplify().simplify(child).getResult());
            }
        }
        if(!eqTree.data.isOrdered()){
            Collections.sort(newChildren);
        }
        List<Tree<MathObject>> newOperands = new ArrayList<>();
        for(Equation newChild : newChildren){
            newOperands.add(newChild.tree);
        }
        eqTree.setChildren(newOperands);
        return new SimplifierResult(new Equation(eqTree));
    }
}
