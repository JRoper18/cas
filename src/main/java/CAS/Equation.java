package CAS;

import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by jack on 12/30/2016.
 */
public class Equation implements Serializable, Comparable<Equation>{
    public Tree<MathObject> tree;
    public Equation(Tree<MathObject> tree){
        this.tree = tree;
    }
    public Equation(String str){
        this.tree = EquationBuilder.makeEquation(str).tree;
    }
    public Equation(Equation prev){
        this.tree = prev.tree;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Equation){
            return this.tree.equals(((Equation) obj).tree);
        }
        return false;
    }

    @Override
    public String toString(){
        if(this.tree.data.getArgs() == 0){ //No children
            return this.tree.data.toString();
        }
        else{
            String build = this.tree.data.toString();
            build += (" ( ");
            for(Tree<MathObject> child : this.tree.getChildren()){
                build += (new Equation(child).toString() + " , ");
            }
            //Remove the last comma and add an end paren
            build = build.substring(0, build.length() - 3);
            build += " ) ";
            return build;
        }
    }
    public Equation clone(){
        return new Equation(this);
    }
    public MathObject getRoot(){
        return this.tree.data;
    }
    public Equation getSubEquation(int index){
        return new Equation(this.tree.getChild(index));
    }
    public boolean isUndefined(){
        return this.getRoot().equals(new MathObject(MathOperator.UNDEFINED));
    }
    public int complexity(){
        return this.tree.size();
    }
    public boolean isType(SimplificationType type){
        return Identifier.isType(this, type);
    }

    public int compareTo(Equation equation){ //Meant to satisfy the triangle operator, Joel Cohen's book pdf page 104/5
        Equation eq1 = this.clone();
        Equation eq2 = equation.clone();
        if(!this.isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)){
           eq1 = Simplifier.simplify(this, SimplificationType.AUTOSIMPLIFIED_EXPRESSION);
        }
        if(!equation.isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)){
            eq2 = Simplifier.simplify(equation, SimplificationType.AUTOSIMPLIFIED_EXPRESSION);
        }
        if((eq1.isType(SimplificationType.INTEGER) || eq1.isType(SimplificationType.FRACTION_STANDARD_FORM)) && (eq2.isType(SimplificationType.FRACTION_STANDARD_FORM)) || eq2.isType(SimplificationType.INTEGER)){
            BigDecimal eq1Num = (eq1.getRoot().getOperator()==MathOperator.FRACTION)? new BigDecimal(((MathInteger) eq1.getSubEquation(0).getRoot()).num).divide(new BigDecimal(((MathInteger) eq1.getSubEquation(1).getRoot()).num)) : new BigDecimal(((MathInteger) eq1.getRoot()).num);
            BigDecimal eq2Num = (eq2.getRoot().getOperator()==MathOperator.FRACTION)? new BigDecimal(((MathInteger) eq2.getSubEquation(0).getRoot()).num).divide(new BigDecimal(((MathInteger) eq2.getSubEquation(1).getRoot()).num)) : new BigDecimal(((MathInteger) eq2.getRoot()).num);
            return eq1Num.compareTo(eq2Num);
        }
        else if(eq1.getRoot().getOperator() == MathOperator.EXPRESSION && eq2.getRoot().getOperator() == MathOperator.EXPRESSION){
            return ((GenericExpression) eq1.getRoot()).tag.compareTo(((GenericExpression) eq2.getRoot()).tag);
        }
        else if((eq1.getRoot().getOperator() == MathOperator.ADD && eq2.getRoot().getOperator() == MathOperator.ADD) || (eq1.getRoot().getOperator() == MathOperator.MULTIPLY && eq2.getRoot().getOperator() == MathOperator.MULTIPLY)){
            int eq1Children = eq1.tree.getNumberOfChildren();
            int eq2Children = eq2.tree.getNumberOfChildren();
            for(int offset = 0; offset < Math.min(eq1Children, eq2Children); offset++){
                if(!eq1.getSubEquation(eq1Children - offset).equals(eq2.getSubEquation(eq2Children -offset))) {
                    return eq1.getSubEquation(eq1Children - offset).compareTo(eq2.getSubEquation(eq2Children - offset));
                }
            }
            return new Integer(eq1Children).compareTo(new Integer(eq2Children));
        }
        else if(eq1.getRoot().getOperator() == MathOperator.POWER && eq2.getRoot().getOperator() == MathOperator.POWER){
            if(!new Equation("BASE(" + eq1 + ")").equals(new Equation("BASE(" + eq2 + ")"))){
                return new Equation("BASE(" + eq1 + ")").compareTo(new Equation("BASE(" + eq2 + ")"));
            }
           return new Equation("EXPONENT(" + eq1 + ")").compareTo(new Equation("EXPONENT(" + eq2 + ")"));
        }
        else if(eq1.getRoot().getOperator() == MathOperator.FACTORIAL && eq2.getRoot().getOperator() == MathOperator.FACTORIAL){
            return eq1.getSubEquation(0).compareTo(eq2.getSubEquation(0));
        }
        return 0;//DEFAULT, CHANGETHIS
    }
}
