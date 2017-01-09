package CAS.EquationObjects;

/**
 * Created by jack on 12/31/2016.
 */
public class SyntaxObject extends EquationObject {
    public SyntaxObjectType syntax;
    public SyntaxObject(SyntaxObjectType type){
        super(0);
        this.syntax = type;
    }
}
