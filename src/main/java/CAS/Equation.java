package CAS;

import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;

import java.io.Serializable;

/**
 * Created by jack on 12/30/2016.
 */
public class Equation implements Serializable{
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
}
