package EquationObjects;

import java.util.HashMap;

/**
 * Created by jack on 12/31/2016.
 */
public class SyntaxObjectAbbriviations {
    public static final HashMap<String, SyntaxObjectType> abbriviations;
    static {
        abbriviations = new HashMap<>();
        abbriviations.put(")", SyntaxObjectType.CLOSE_PAREN);
        abbriviations.put("(", SyntaxObjectType.OPEN_PAREN);
        abbriviations.put(",", SyntaxObjectType.COMMA);

    }
}
