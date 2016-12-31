package EquationObjects.PatternMatching;

import EquationObjects.EquationObject;

/**
 * Created by jack on 12/29/2016.
 */
public class LogicalOperator extends EquationObject{
    public LogicalOperatorType operator;
    public LogicalOperator(LogicalOperatorType type){
        this.operator = type;
    }
}

