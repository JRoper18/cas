/**
 * Created by jack on 1/4/2017.
 */
public class EquationCondition {
    public Equation arg1;
    public Equation arg2;
    public final EquationConditionType type;
    public EquationCondition(Equation arg1, EquationConditionType type, Equation arg2){
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.type = type;
    }
    public boolean fitsCondition(){
        PatternMatcher matcher = new PatternMatcher();
        switch(type){
            case EQUALS_FULL: //Doesn't allow for generics to match with anything. The trees must be exactly the same.
                return arg1.tree.equals(arg2.tree);
            case NOT_EQUALS_FULL:
                return !arg1.tree.equals(arg2.tree);
        }
        return false;//CHANGE THIS
    }
}
