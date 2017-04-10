package CAS;

import CAS.EquationObjects.*;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 12/30/2016.
 */
public class Equation implements Serializable, Comparable<Equation>{
    public Tree<MathObject> tree;
    public Equation(Tree<MathObject> tree){
        this.tree = tree.clone();
    }
    public Equation(Tree<MathObject> tree, int autoSimplifyLevel){
        this.tree = EquationBuilder.simplifyTree(tree.clone(), autoSimplifyLevel).tree;
    }
    public static Equation fromList(Equation list){
        if(list.isType(MathOperator.LIST)){
            return list;
        }
        return new Equation("LIST(" + list.toString() + ")", 0);
    }
    public static Equation fromList(List<Equation> list, MathOperator newRoot){
        String build = "";
        for(Equation eq : list){
            build += eq.toString() + ",";
        }
        if(list.size() == 0){
            return new Equation("LIST()", 0);
        }
        build = build.substring(0, build.length() - 1); //Remove last paren
        Equation listEq = new Equation("LIST(" + build + ")", 0);
        listEq.tree.data = new MathObject(newRoot);
        return listEq;
    }
    public static Equation fromList(List<Equation> list){
        return fromList(list, MathOperator.LIST);
    }
    public List<Equation> toList(){
        if(!this.isType(MathOperator.LIST)){
            throw new UncheckedIOException(new IOException("This equation is not a list!"));
        }
        return this.getOperands();
    }
    public Equation(String str){
        this.tree = EquationBuilder.makeEquation(str).tree;
    }
    public Equation(String str, int autoSimplifyLevel){
        this.tree = EquationBuilder.makeEquation(str, autoSimplifyLevel).tree;
    }
    public Equation(Equation prev){
        this.tree = prev.tree.clone();
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
    public List<Equation> getOperands(){
        List<Equation> toReturn = new ArrayList<>();
        for(Tree<MathObject> child : this.tree.getChildren()){
            toReturn.add(new Equation(child));
        }
        return toReturn;
    }
    public boolean isUndefined(){
        return this.getRoot().equals(new MathObject(MathOperator.UNDEFINED));
    }
    public int complexity(){
        return this.tree.size();
    }
    public boolean isType(MathOperator type){
        return this.getRoot().getOperator() == type;
    }
    public boolean isType(MathOperatorSubtype type){
        return this.getRoot().getOperator().getSubType() == type;
    }
    public boolean isType(IdentificationType type){
        return Identifier.isType(this, type);
    }

    public int compareTo(Equation equation){ //Meant to satisfy the triangle operator, Joel Cohen's book pdf page 104/5
        //Or, if you don't read, this basically defines a predefined order for which arguements should be in.
        Equation eq1 = this.clone();
        Equation eq2 = equation.clone();
        MathOperator eq1Op = eq1.getRoot().getOperator();

        MathOperator eq2Op = eq2.getRoot().getOperator();
        if((eq1.isType(IdentificationType.INTEGER) || eq1.isType(IdentificationType.FRACTION_STANDARD_FORM)) && ((eq2.isType(IdentificationType.FRACTION_STANDARD_FORM)) || eq2.isType(IdentificationType.INTEGER))){
            BigDecimal eq1Num = (eq1Op==MathOperator.FRACTION)? new BigDecimal(((MathInteger) eq1.getSubEquation(0).getRoot()).num).divide(new BigDecimal(((MathInteger) eq1.getSubEquation(1).getRoot()).num)) : new BigDecimal(((MathInteger) eq1.getRoot()).num);
            BigDecimal eq2Num = (eq2Op==MathOperator.FRACTION)? new BigDecimal(((MathInteger) eq2.getSubEquation(0).getRoot()).num).divide(new BigDecimal(((MathInteger) eq2.getSubEquation(1).getRoot()).num)) : new BigDecimal(((MathInteger) eq2.getRoot()).num);
            return eq1Num.compareTo(eq2Num);

        }
        else if(eq1Op == MathOperator.EXPRESSION && eq2Op == MathOperator.EXPRESSION){
            if(eq1.isType(IdentificationType.VARCONSTANT) && !eq2.isType(IdentificationType.VARCONSTANT)){
                return -1;
            }
            if(eq2.isType(IdentificationType.VARCONSTANT) && !eq1.isType(IdentificationType.VARCONSTANT)){
                return 1;
            }
            return ((GenericExpression) eq1.getRoot()).tag.compareTo(((GenericExpression) eq2.getRoot()).tag);
        }
        else if((eq1Op == MathOperator.ADD && eq2Op == MathOperator.ADD) || (eq1Op == MathOperator.MULTIPLY && eq2Op == MathOperator.MULTIPLY)){

            int eq1Children = eq1.tree.getNumberOfChildren();
            int eq2Children = eq2.tree.getNumberOfChildren();
            for(int offset = 1; offset <= Math.min(eq1Children, eq2Children); offset++){
                if(!eq1.getSubEquation(eq1Children - offset).equals(eq2.getSubEquation(eq2Children -offset))) {
                    return eq1.getSubEquation(eq1Children - offset).compareTo(eq2.getSubEquation(eq2Children - offset));
                }
            }
            return new Integer(eq1Children).compareTo(new Integer(eq2Children));
        }
        else if(eq1Op == MathOperator.POWER && eq2Op == MathOperator.POWER){
            if(!new Equation("BASE(" + eq1 + ")", 1).equals(new Equation("BASE(" + eq2 + ")", 1))){
                return new Equation("BASE(" + eq1 + ")", 1).compareTo(new Equation("BASE(" + eq2 + ")", 1));
            }
           return new Equation("EXPONENT(" + eq1 + ")", 1).compareTo(new Equation("EXPONENT(" + eq2 + ")", 1));
        }
        else if(eq1Op == MathOperator.FACTORIAL && eq2Op == MathOperator.FACTORIAL){
            return eq1.getSubEquation(0).compareTo(eq2.getSubEquation(0));
        }
        else if(eq1Op == MathOperator.CUSTOM_FUNCTION && eq2Op == MathOperator.CUSTOM_FUNCTION){
            return ((CustomFunction) eq1.getRoot()).functionName.compareTo(((CustomFunction) eq2.getRoot()).functionName);
        }
        else if(!(eq1Op == MathOperator.MULTIPLY || eq1Op == MathOperator.POWER || eq1Op == MathOperator.ADD || eq1Op == MathOperator.FACTORIAL || eq1Op == MathOperator.CUSTOM_FUNCTION || eq1Op.getSubType()==MathOperatorSubtype.SYMBOL)){
            return eq1Op.toString().compareTo(eq2Op.toString());
        }
            else if((eq1.isType(IdentificationType.INTEGER) || eq1.isType(IdentificationType.FRACTION_STANDARD_FORM)) && !(eq2.isType(IdentificationType.INTEGER) || eq2.isType(IdentificationType.FRACTION_STANDARD_FORM))){
            return -1;
        }
        else if(eq1Op == MathOperator.MULTIPLY && (eq2Op == MathOperator.POWER || eq2Op == MathOperator.ADD || eq2Op == MathOperator.FACTORIAL || eq2Op == MathOperator.CUSTOM_FUNCTION || eq2Op.getSubType()==MathOperatorSubtype.SYMBOL)){
            return this.compareTo(new Equation("TIMES ( 1, " + eq2 + ")", 0));
        }
        else if(eq1Op == MathOperator.POWER && (eq2Op == MathOperator.ADD || eq2Op == MathOperator.FACTORIAL || eq2Op == MathOperator.CUSTOM_FUNCTION || eq2Op.getSubType()==MathOperatorSubtype.SYMBOL)){
            return this.compareTo(new Equation("POWER(" + eq2 + ",1)", 0));
        }
        else if(eq1Op == MathOperator.ADD && (eq2Op == MathOperator.FACTORIAL || eq2Op == MathOperator.CUSTOM_FUNCTION || eq2Op.getSubType()==MathOperatorSubtype.SYMBOL)){
            return this.compareTo(new Equation("ADD ( 0, " + eq2 + ")", 0));
        }
        else if(eq1Op == MathOperator.FACTORIAL && (eq2Op == MathOperator.CUSTOM_FUNCTION || eq2Op.getSubType()==MathOperatorSubtype.SYMBOL)){
            if(eq1.getSubEquation(0).equals(eq2)){
                return -1;
            }
            return this.compareTo(new Equation("FACTORIAL(" + eq2 + ")",0));
        }
        else if(eq1Op == MathOperator.CUSTOM_FUNCTION && eq2Op.getSubType() == MathOperatorSubtype.SYMBOL){
            if(eq1Op.toString().equals(eq2Op.toString())){
                return -1;
            }
            return eq1Op.toString().compareTo(eq2Op.toString());
        }
        else if(eq1.isType(MathOperatorSubtype.SYMBOL) && eq2.isType(MathOperatorSubtype.SYMBOL)){
            return eq1.getRoot().toString().compareTo(eq2.getRoot().toString());
        }
        else if(eq1.isType(MathOperator.LIST)){
            //Count children
            List<Equation> oper1 = eq1.getOperands();
            List<Equation> oper2 = eq2.getOperands();
            if(oper1.size() > oper2.size()){
                return 1;
            }
            if(oper1.size() > oper2.size()){
                return -1;
            }

            //Compare children
            for(int i = 0; i<oper1.size(); i++){
                int num = oper1.get(i).compareTo(oper2.get(i));
                if(num != 0){
                    return num;
                }
            }
            //Children are the same. They are the same.
            return 0;
        }
        return -1 * equation.compareTo(this);
    }
}
