package Simplification.Methods;

import CAS.Equation;
import CAS.Tree;
import Simplification.SimplifierResult;
import Simplification.SimplifierStrategy;
import Simplification.SimplifierTree;
import Simplification.SubstitutionData;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public class BruteForceRemoveOperator implements SimplifierStrategy{
    public int maxLevel;
    public BruteForceRemoveOperator(int maxLevel){
        this.maxLevel = maxLevel;
    }
    public BruteForceRemoveOperator(){
        this.maxLevel = 5;
    }
    @Override
    public SimplifierResult simplify(Equation eq) {
        SimplifierTree tree = new SimplifierTree(new SubstitutionData(null, eq.clone()));
        for(int level = 0; level < this.maxLevel; level++){
            List<Tree<SubstitutionData>> currentLevel = tree.getLevelChildren(level);
            for(int i = 0; i<currentLevel.size(); i++){
                SimplifierTree currentNode = (SimplifierTree) currentLevel.get(i);
                Equation currentEq = currentNode.data.equation;
                //Check if the node is good.
                List<LinkedList<Integer>> pathsToFunc = currentEq.tree.findPaths(eq.getRoot());
                if(pathsToFunc.isEmpty()){
                    //No paths to the root operator we are trying to remove. That means it's not there! Yay!
                    return currentNode.getResult();
                }
                for(LinkedList<Integer> path: pathsToFunc){
                    Equation subEquation = new Equation(currentEq.tree.getChildThroughPath(path), 0);

                }
            }
        }
    }
}
