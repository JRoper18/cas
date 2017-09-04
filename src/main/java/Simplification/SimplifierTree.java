package Simplification;

import CAS.Equation;
import Util.Tree;
import Substitution.EquationSub;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import java.util.ArrayList;
import java.util.Iterator;
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
        Tree<SubstitutionData> selected = tree;
        Tree<SubstitutionData> parent = selected.getParent();
        LinkedList<SubstitutionData> path = new LinkedList<>();
        while(parent != null){
            path.add(selected.data);
            selected = parent;
            parent = selected.getParent();
        }
        return new SimplifierResult(path);
    }

}
