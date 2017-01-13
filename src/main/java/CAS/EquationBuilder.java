package CAS;

import CAS.EquationObjects.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class EquationBuilder{
    public static Equation makeEquation(String str){
        return toCorrectForm(new Equation(makeEquationTree(str)));
    }
    public static Equation makeUnprocessedEquation(String str){
        return new Equation(makeEquationTree(str));
    }
    private static Tree<MathObject> makeEquationTree(String equationStr) { //This stakes a string input of which I hope is correctly formatted.
        List<Object> equationObjectList = preProcess(equationStr);
        Tree<MathObject> tree = new Tree<>();
        Tree<MathObject> selected = tree;
        for (Object equationObject : equationObjectList) {
            if (equationObject instanceof SyntaxTerminator) {
                selected = selected.getParent();
                selected.removeChild(selected.getNumberOfChildren() - 1);
                if (selected.isRoot()) {
                    break;
                }
                MathObject operator = (MathObject) selected.data;
                //Check the number of args isn't too little.
                if (operator.getArgs() > selected.getNumberOfChildren()) {
                    throw new UncheckedIOException(new IOException("You have the wrong number of arguments for the operator: " + selected.data + ". It takes " + operator.getArgs() + " arguments, but you put in too little. "));
                }
                selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren() - 1);
            } else if (equationObject instanceof RationalTempInfoHolder) {  //It's not syntax. Is it a temporary info holder?
                selected.data = new MathObject(MathSymbol.DIVIDE);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).numer);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).denom);
            } else {
                MathObject current = (MathObject) equationObject;
                selected.data = current;
                if (current.getArgs() > 0){
                    if (!selected.isRoot()) {
                        selected.getParent().addEmptyChild();
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren() - 1);
                    } else {
                        selected.addEmptyChild();
                        selected = selected.getChild(0);
                    }
                } else {
                    if(!selected.isRoot()){
                        selected.getParent().addEmptyChild();
                        selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren() - 1);
                    }
                }
            }
        }
        if(tree.containsData(null)){
            throw new UncheckedIOException(new IOException("You missed an ending parenthesis in the equation: " + equationStr));
        }
        return tree;
    }
    private static List<Object> preProcess(String equationStr){
        String newStr = equationStr.replace(")", " )");
        String[] tokens = newStr.trim().split("[ (,]");
        List<Object> equationObjectList = new ArrayList<>();
        for(int i = 0; i<tokens.length; i++){
            Object next = parseString(tokens[i]);
            if(next != null){
                equationObjectList.add(next);
            }
        }
        return equationObjectList;
    }
    private static Equation toCorrectForm(Equation eq){
        List<EquationSub> subs = new ArrayList<>();
        subs.add(new StructuralSub(makeUnprocessedEquation("MINUS ( _v1 , _v2 )"), makeUnprocessedEquation("PLUS ( _v1 , TIMES ( -1 , _v2 ) )")));
        subs.add(new StructuralSub(makeUnprocessedEquation("DIVIDE( _v1 , _v2 )"), makeUnprocessedEquation("FRACTION ( _v1 , _v2 )"),makeUnprocessedEquation("AND ( EQUALS ( TYPEOF ( _v1 ) , NUMBER ) , EQUALS ( TYPEOF ( _v2 ) , NUMBER ) , NOT ( EQUALS ( _v2 , 0 ) ) )"))); //Division between two ints
        subs.add(new StructuralSub(makeUnprocessedEquation("DIVIDE ( _v1 , _v2 )"), makeUnprocessedEquation("TIMES ( _v1 , FRACTION ( 1 , _v2 ) )"), makeUnprocessedEquation("AND ( EQUALS ( TYPEOF ( _v1 ) , EXPRESSION ) , EQUALS ( TYPEOF ( _v2 ) , NUMBER ) )"))); //Denominator is an int
        subs.add(new StructuralSub(makeUnprocessedEquation("DIVIDE ( _v1 , _v2 )"), makeUnprocessedEquation("TIMES ( _v1 , POWER ( _v2 , -1 ) )")));
        for(EquationSub sub : subs){
            eq = sub.applyEverywhere(eq);
        }
        return eq;
    }
    public static Object parseString(String str){
        if(str.length() == 0){
            return null;
        }
        //Check it it's a terminator
        if(str.equals(")")){
            return new EquationBuilder().new SyntaxTerminator();
        }
        //Next, check for numbers
        try{
            double num = Double.parseDouble(str);
            if(Math.floor(num) == num){ //Easy way to check if num is an int
                return new MathInteger((int) num);
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
            return new EquationBuilder().new RationalTempInfoHolder(new MathInteger(numer), new MathInteger(denom));
        } catch(NumberFormatException e){
            //IGNORE IT
        }
        //Maybe it's a generic. If it's an expression it'll look like _<genericName>
        if(str.charAt(0) == '_'){ //There's a _ so it's a generic.
            // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
            if(str.length() == 1){ //It's just a _, which means any expression of any type with any name.
                return new GenericExpression();
            }
            String name = str.substring(1, str.length());
            GenericExpression genEx = new GenericExpression(name);
            if(str.charAt(1) == '_'){
                //Double underscore means it's named
                genEx.named = true;
            }
            return genEx;
        }
        //It's not a generic, so we don't need to base case sensitive at this point
        str = str.toUpperCase();
        //Check the list of mathematical operators.
        try{
            MathObject obj = new MathObject(MathSymbol.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }

        //Check the abbriviations table
        MathObject possibleObject = (MathObjectAbbriviations.abbriviations.get(str));
        if(possibleObject != null){ //A HashMap's .get() method returns null if there's no key.
            return possibleObject;
        }
        throw new UncheckedIOException(new IOException("Operator: " + str + " is not recognized by CAS.EquationBuilder. "));
    }
    private class RationalTempInfoHolder{
        public MathInteger numer;
        public MathInteger denom;
        public RationalTempInfoHolder(MathInteger numer, MathInteger denom){
            this.numer = numer;
            this.denom = denom;
        }

        @Override
        public boolean equals(Object n){
            if(n instanceof RationalTempInfoHolder){
                return this.numer.equals(((RationalTempInfoHolder) n).numer) && this.denom.equals(((RationalTempInfoHolder) n).denom);
            }
            return false;
        }
    }
    private class SyntaxTerminator{}
}