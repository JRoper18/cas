package CAS;

import java.io.Serializable;

/**
 * Created by jack on 1/4/2017.
 */
public interface DirectOperation extends Serializable{
    Equation operate(Equation eq);
}
