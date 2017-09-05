package Database;

/**
 * Created by Jack Roper on 3/17/2017.
 */
public class SubstitutionRuleDatabase {
    public static final String[] rules = {
            "DERIV(POWER(_x_EXPRESSION, _n_VARCONSTANT), _d) -> TIMES(_n, POWER(_x, MINUS(_n, 1)), DERIV(_x, _d))",
            "DERIV(_#1, _#2) -> 0",
            "DERIV(_#1, _#1) -> 1",
            "DERIV(_n_VARCONSTANT, _x_EXPRESSION) -> 0",
            "DERIV(_x_EXPRESSION, _x_EXPRESSION) -> 1",
            "DERIV(TIMES(_n_VARCONSTANT, _f_EXPRESSION), _#1) -> TIMES(_n, DERIV(_f, _#1))",
            "DERIV(TIMES(_n_VARCONSTANT, _#1), _#1) -> _n",
            "DERIV(TIMES(_f_EXPRESSION, _g_EXPRESSION), _d) -> PLUS(TIMES(DERIV(_f, _d), _g), TIMES(DERIV(_g, _d), _f))",
            "DERIV(PLUS(_f_EXPRESSION, _g_EXPRESSION), _d) -> PLUS(DERIV(_f, _d), DERIV(_g, _d))",
            "DERIV(POWER(E, _f_EXPRESSION), _d) -> TIMES(DERIV(_f_EXPRESSION, _d), POWER(E, _f))",
            "DERIV(POWER(_n_VARCONSTANT, _f_EXPRESSION), _d) -> TIMES(NATURAL_LOG(_n), DERIV(_f_EXPRESSION, _d), POWER(_n, _f))",
            "DERIV(SIN(_x_EXPRESSION), _d) -> TIMES(-1, DERIV(_x_EXPRESSION, _d), COS(_x_EXPRESSION))",
            "DERIV(COS(_x_EXPRESSION), _d) -> TIMES(DERIV(_x_EXPRESSION, _d), SIN(_x_EXPRESSION))"
    };
}
