package Simplification.Methods;

import CAS.Equation;
import CAS.EquationObjects.MathOperator;
import Simplification.*;
import Util.Tree;
import Database.DatabaseConnection;
import Database.SubSerializer;
import Substitution.EquationSub;

import java.sql.ResultSet;
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
    public SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException {
        Tree<SubstitutionData> tree = new Tree<>(new SubstitutionData(null, eq.clone()));
        for(int level = 0; level < this.maxLevel; level++){
            List<Tree<SubstitutionData>> currentLevel = tree.getLevelChildren(level);
            for(int i = 0; i<currentLevel.size(); i++){
                Tree<SubstitutionData> currentNode = (currentLevel.get(i));
                Equation currentEq = currentNode.data.equation;
                //Check if the node is good.
                if(!currentEq.tree.containsData(eq.getRoot())){
                    //No paths to the root operator we are trying to remove. That means it's not there! Yay!
                    return SimplifierTree.getResult(currentNode);
                }
                try {
                    Equation tempEq = currentEq.clone();
                    ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + eq.getRoot() + "')");
                    while (results.next()) {
                        EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                        Equation subbedEq = tempSub.applyEverywhere(tempEq);
                        SubstitutionData data = new SubstitutionData(tempSub, Simplifier.simplifyWithMetaFunction(subbedEq, MathOperator.AUTOSIMPLIFY));
                        List<LinkedList<Integer>> pathsToDups = tree.findPaths((tree1 -> {
                            if(tree1.data instanceof SubstitutionData){
                                return ((SubstitutionData) tree1.data).equation.equals(data.equation);
                            }
                            return false;
                        }));
                        if(pathsToDups.size() == 0){ //No duplicates
                            currentNode.addChildWithData(data);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw new SimplifyObjectiveNotDoneException(this);
    }
}
