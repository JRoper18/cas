package Simplification;

import CAS.Equation;
import Util.Tree;
import Substitution.EquationSub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public class SimplifierTree {
    public static SimplifierResult getResult(Tree<SubstitutionData> tree){
        if(tree.isRoot()){
            return new SimplifierResult(tree.data.equation);
        }
        LinkedList<Integer> path = tree.pathFromRoot();
        List<Equation> changes = new ArrayList<Equation>();
        List<EquationSub> subs = new ArrayList<EquationSub>();
        Tree<SubstitutionData> selected = tree.getRoot().getChild(path.get(0));
        Equation initial = tree.getRoot().data.equation;
        for(int i = 1; i<path.size(); i++){
            changes.add(selected.data.equation);
            subs.add(selected.data.sub);
            selected = selected.getChild(path.get(i));
        }
        return new SimplifierResult(initial, tree.data.equation, subs, changes);
    }

}
