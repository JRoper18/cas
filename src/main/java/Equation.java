import EquationObjects.MathObjects.MathObject;

/**
 * Created by jack on 12/30/2016.
 */
public class Equation {
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
}
