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
public class SimplifierTree extends Tree<SubstitutionData> {
    public boolean closed;
    public int complexity;

    public SimplifierTree(SubstitutionData data){
        this.data = data;
        this.closed = false;
        this.complexity = data.equation.complexity();
    }
    public SimplifierResult getResult(){
        if(this.isRoot()){
            return new SimplifierResult(this.data.equation);
        }
        LinkedList<Integer> path = this.pathFromRoot();
        List<Equation> changes = new ArrayList<Equation>();
        List<EquationSub> subs = new ArrayList<EquationSub>();
        SimplifierTree selected = (SimplifierTree) this.getRoot();
        for(int i = 0; i<path.size(); i++){
            changes.add(selected.data.equation);
            subs.add(selected.data.sub);
            selected = (SimplifierTree) selected.getChild(path.get(i));
        }
        return new SimplifierResult(changes.get(0), this.data.equation, subs, changes);
    }
    public static SimplifierTree fromTree(Tree<SubstitutionData> tree){
        SimplifierTree newTree = new SimplifierTree(tree.data);
        for(Tree<SubstitutionData> child: tree.getChildren()){
            newTree.addChild(fromTree(child));
        }
        return newTree;
    }

}
