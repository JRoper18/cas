package Simplification.Methods;

import CAS.Equation;
import Simplification.SimplifierStrategy;

/**
 * Created by Jack Roper on 9/4/2017.
 */
public abstract class RemoveAllOperatorsStrategy extends SimplifierStrategy{
    public boolean isSimplifyDone(Equation begin, Equation current){
        return !current.tree.containsData(begin.getRoot());
    }
}
