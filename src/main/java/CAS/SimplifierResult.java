package CAS;

import java.util.List;

/**
 * Created by Jack Roper on 4/3/2017.
 */
public class SimplifierResult {
    public Equation initial;
    public Equation result;
    public List<EquationSub> subsUsed;
    public List<Equation> changes;
    public SimplifierResult(Equation initial, Equation eq, List<EquationSub> subs, List<Equation> changes){
        this.initial = initial;
        this.result = eq;
        this.subsUsed = subs;
        this.changes = changes;
    }
    public String steps(){
        StringBuilder build = new StringBuilder();
        build.append(initial.toString());
        build.append("\n");
        for(int i = 0; i<this.changes.size(); i++){
            Equation step = this.changes.get(i);
            EquationSub sub = this.subsUsed.get(i);

            build.append(step.toString());
            build.append(" using sub: " + sub.toString());
            build.append("\n");
        }
        build.append(result.toString());
        return build.toString();
    }
}
