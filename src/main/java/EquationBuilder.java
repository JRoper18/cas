import EquationObjects.EquationObject;
import EquationObjects.MathObjects.*;
import EquationObjects.PatternMatching.GenericExpression;
import EquationObjects.PatternMatching.GenericType;
import EquationObjects.PatternMatching.LogicalOperator;
import EquationObjects.PatternMatching.LogicalOperatorType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidParameterException;

public class EquationBuilder{
    public static Equation makePatternEquation(String equationStr){
        return null;
    }
    public static Equation makeEquation(String equationStr){
        return null;
    }
    public static EquationObject parseString(String str){
        return parseString(str, true);
    }
    public static EquationObject parseString(String str, boolean allowPattern){
        //First, check for numbers
        try{
            double num = Double.parseDouble(str);
            if(Math.floor(num) == num){ //Easy way to check if num is an int
                return new MathNumberInteger((int) num);
            }
            //Nope, it's decimal. We hate decimal numbers, turn them into rational numbers.
            //Remove the decimal place and turn it into a fraction.
            /*
            3.7 = 37/10
            3.503 = 3503 / 1000
            345.234 = 345234 / 1000
             */
            int afterDecLength = str.length() - str.indexOf('.') - 1;

            int denom = (int) Math.pow(10, afterDecLength);
            int numer = (int) (num * denom);
            return new MathNumberRational(numer, denom);
        } catch(NumberFormatException e){
            //IGNORE IT
        }
        //Done checking for numbers. Check for operators
        if(allowPattern){ //If we are allowing pattern objects, check for logical operators
            switch(str){
                case "||":
                    return new LogicalOperator(LogicalOperatorType.OR);
                case "&&":
                    return new LogicalOperator(LogicalOperatorType.AND);
                default:
            }
            //Nope, not logical. Maybe it's a generic. If it's an expression it'll look like <genericType>_<genericName>
            int index = str.indexOf('_');
            if(index != -1){ //There's a _ so it's a generic.
                // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
                if(str.length() == 1){ //It's just a _, which means any expression of any type with any name.
                    return new GenericExpression();
                }
                String name = str.substring(index + 1, str.length());
                if(index == 0){ //They have nothing behind the _, so no type is specified.
                    return new GenericExpression(name);
                }
                String typeStr = str.substring(0, index);
                GenericType type;
                try{
                    type = GenericType.valueOf(typeStr);
                } catch (InvalidParameterException er){
                    throw new UncheckedIOException(new IOException("You put in an invalid generic expression type: " + typeStr));
                }
                if(index == str.length()-1){ //The _ is at the back, so no name specified.
                    return new GenericExpression(type);
                }

                //We've gotten here, so it must have both a name and a type defined.
                return new GenericExpression(type, name);
            }
        }
        //Check the list of mathematical operators.
        try{
            MathObjectNamed obj = new MathObjectNamed(MathOperators.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }
        //OK, now check the abbriviations table.
        MathOperatorsAbbriviations abbriviations = new MathOperatorsAbbriviations();
        return new MathObjectNamed(abbriviations.abbriviations.get(str));
    }
}