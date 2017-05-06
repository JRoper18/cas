package CAS;

import CAS.EquationObjects.*;
import Database.EquationSubDatabase;

import javax.management.AttributeList;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class EquationBuilder{
    public static Equation makeEquation(String str, int autoSimplifyLevel){
        return simplifyTree(makeEquationTree(str), autoSimplifyLevel);
    }
    public static Equation simplifyTree(Tree<MathObject> tree, int autoSimplifyLevel){
        Equation eq = new Equation(tree);
        Equation processedEquation;
        if(autoSimplifyLevel == 0){
            return new Equation(tree);
        }
        else{
            processedEquation = simplifyTree(tree, autoSimplifyLevel-1);
            if(processedEquation.tree.containsData(new MathObject(MathOperator.UNDEFINED))){
                return new Equation("UNDEFINED", 0);
            }
            switch(autoSimplifyLevel){
                case 1: //Only do meta-functions.
                    return Simplifier.simplifyMetaFunctions(processedEquation);
                case 2: //Autosimplify this
                    processedEquation = Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.SIMPLIFY_RATIONAL_EXPRESSION);
                    processedEquation = Simplifier.orderEquation(processedEquation);
                    return Simplifier.orderEquation(Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.AUTOSIMPLIFY));
                default:
                    return processedEquation;
            }
        }
    }
    public static String infixToPrefix(String str){
        String newStr =str.trim().replaceAll(" ", "");
        String[] tokens = newStr.split(generateStringSplitRegex());
        List<String> newList = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for(String token: tokens) {
            if(token.charAt(0) == '_' || Character.isDigit(token.charAt(0))){ //Variable or number
                newList.add("(");
                newList.add(token);
            }
            else if(token.charAt(token.length()-1) == '('){ //It's either a prefix function or a plain open paren
                if(token.length() == 1){ //Open paren
                    stack.push(token);
                }
                else { //Function
                    stack.push(token);
                }
            }
            else if(token.equals(",")){
                newList.add(")");
            }
            else if(token.equals(")")){
                String unstackedToken = stack.peek();
                while(!stack.isEmpty() && unstackedToken != "("){
                    unstackedToken = stack.pop();
                    newList.add(")");
                    newList.add(unstackedToken);
                }
            }
            else{ //It's an infix operator
                newList.add(")");
                stack.push(token);
            }
        }
        while(!stack.isEmpty()){
            newList.add(stack.pop());
            newList.add(")");
        }
        Collections.reverse(newList);
        StringBuilder unprocessed = new StringBuilder(String.join(" ", newList));
        System.out.println(unprocessed);
        Set<Integer> parenLocations = new HashSet<>();
        for(int i = 0; i<unprocessed.length(); i++){
            if(unprocessed.charAt(i) == ')' ){
                unprocessed.setCharAt(i, '(');
                parenLocations.add(i);
            }
        }
        int offset = 0;
        for(int i = 0; i<unprocessed.length(); i++){
            if(unprocessed.charAt(i) == '(' && !parenLocations.contains(i - offset) && unprocessed.charAt(i-1) == ' '){
                unprocessed.setCharAt(i, ')');
                unprocessed.insert(i+1, ',');
                offset++;
            }
        }
        unprocessed.deleteCharAt(unprocessed.length()-1); //Remove the last extra comma
        System.out.println(unprocessed.toString());
        return newList.toString();
    }

    /**
     * Creates a regex expression that splits an input string into seperate equation parts to be processed
     * @return A Regex string that will break up any equation string input into it's parts
     */
    private static String generateStringSplitRegex(){
        String innerBuild = "([\\\\)])"; //All equation must be split up with )
        final String specialChars = "\\.[]{}()*+-?^$|/"; //Special characters that require a \\ in order to be put correctly into a string or regex
        for(String key: MathObjectAbbriviations.abbriviations.keySet()){
            if(MathObjectAbbriviations.abbriviations.get(key).type != AbbriviationType.PREFIX){
                //Add all non-prefix functions to be split up
                if(specialChars.indexOf(key) != -1){
                    innerBuild += ("|([\\\\" + key + "])");
                }
                else{
                    innerBuild += ("|([" + key + "])|");
                }
            }
        }
        return "(?<=(" + innerBuild + "|([\\(,])))|(?=(" + innerBuild + "|([,])))";
        //The positive lookbehind and ahead are so that we includ the delimiters in our array when we split the string.

    }
    public static Equation makeEquation(String str){
        return makeEquation(str, 2);
    }
    public static List<Object> makeEquationWithSyntax(String str){
        String newStr = "( " + str.replace(")", " )").trim() + " )";
        String[] tokens = newStr.trim().split("[ ,(]");
        List<Object> eqList = new ArrayList<>();
        for(String token : tokens){
            System.out.println(token);
            if(token.equals(""))
            eqList.add(token);
        }

        return null;
    }
    public static Equation makeUnprocessedEquation(String str){
        return new Equation(makeEquationTree(str));
    }
    private static Tree<MathObject> makeEquationTree(String equationStr) { //This stakes a string input of which I hope is correctly formatted.

        //NOTE TO FUTURE ME: At one point you might try to change this algorithm so that it implements infix notation or something. DO NOT DO THAT. Instead, Change the preprocess function.
        //If you ever think editing this function is a good idea, just remember the all-nighter you pulled trying to fix a bug in it, and where for every bug you fixed 3 more popped up.
        //This is your final warning. DO NOT TOUCH THIS FUNCTION.

        //Note from 3/27/17. Hey, I actually changed it. Not impossible, just a pain in the ass. Wouldn't do it again.
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
                selected.data = new MathObject(MathOperator.FRACTION);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).numer);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).denom);
                selected.replaceWith(new Equation("SIMPLIFY_RATIONAL_FRACTION(" + new Equation(selected) + ")", 1).tree);
                if(!selected.isRoot()){
                    selected.getParent().addEmptyChild();
                }
            } else {
                MathObject current = (MathObject) equationObject;
                selected.data = current;
                if (current.getArgs() > 0 || current.getOperator() == MathOperator.CUSTOM_FUNCTION){
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
        try{
            boolean catchThis = tree.containsData(null);
        } catch(Exception e){
            System.err.println("Missing a parenthesis on your equation: " + equationStr);
        }

        return tree;
    }
    private static List<Object> preProcess(String equationStr){
        String newStr = "( " + equationStr.replace(")", " )").trim() + " )";
        String[] tokens = newStr.trim().split("[ ,(]");
        List<Object> equationObjectList = new ArrayList<>();
        for(int i = 0; i<tokens.length; i++){
            Object next = parseString(tokens[i]);
            if(next != null){
                equationObjectList.add(next);
            }
        }
        //Reorder for infix and postfix to prefix
        List<Object> last;
        List<Object> currentEqList = new ArrayList<>(equationObjectList);
        do {
            last = new ArrayList<>(currentEqList);
            List<Object> newList = new ArrayList<>(currentEqList); //Clone it
            boolean didWeChangeAlready = false;
            for(int i = 0; i<currentEqList.size(); i++){
                Object current = currentEqList.get(i);
                if(current instanceof AbbriviationData){
                    AbbriviationData data = (AbbriviationData) current;
                    if(data.type == AbbriviationType.INFIX){
                        //Find the function right before this one.
                        int beginLastEquationIndex = getLastEquationStartIndex(currentEqList, i);
                        List<Object> previousEquation = currentEqList.subList(beginLastEquationIndex, i);
                        //Now swap places with the previous equation and the operator.
                        List<Object> beforeSwap = new ArrayList<>(currentEqList.subList(0, beginLastEquationIndex)); //Before the swap. Also, cloned.
                        beforeSwap.add(data.op); //Then add the operator and prefix it
                        beforeSwap.addAll(previousEquation); //Then the swapped term
                        beforeSwap.addAll(currentEqList.subList(i+1, currentEqList.size())); //Then add the second term and a syntax terminator.
                        beforeSwap.add(new SyntaxTerminator());
                        newList = beforeSwap;
                        didWeChangeAlready = true;
                        break;
                    }
                }
            }
            currentEqList = newList;
        } while(!last.equals(currentEqList));
        //Now set all abbrivations to operators
        List<Object> newList = new ArrayList<>();
        for(int i = 0; i<currentEqList.size()-1; i++){ //The minus one is because our above process adds extra syntax terminators
            Object current = currentEqList.get(i);
            if(current instanceof AbbriviationData){
                newList.add(((AbbriviationData) current).op);
            }
            else{
                newList.add(current);
            }
        }
        return newList;
    }
    private static int getLastEquationStartIndex(List<Object> eqList, int lastIndex) { //Takes a list and finds the parenthesis matching the last parenthesis, and then returns the equation between them.
        return getLastEquationStartIndex(eqList.subList(0, lastIndex));
    }
    private static int getLastEquationStartIndex(List<Object> eqList){ //Takes a list and finds the parenthesis matching the last parenthesis, and then returns the equation between them.
        int level = 0;
        for(int i = eqList.size() - 1; i >= 0; i--){
            Object current = eqList.get(i);
            if(current instanceof SyntaxTerminator){
                level++;
            }
            else if(current instanceof MathObject){
                if(((MathObject) current).getArgs() > 0){
                    level--;
                }
            }
            else if(current instanceof AbbriviationData){
                AbbriviationData data = (AbbriviationData) current;
                if(data.type == AbbriviationType.PREFIX){
                    if(data.op.getArgs() > 0) {
                        level--;
                    }
                }
                else if(data.type == AbbriviationType.INFIX){ //This will come up in multiple infix operators, like (1+2) + 3 when evaluating the + before the 3.
                    //Find where the previous infix would have it's start index.
                    int lastIndex = getLastEquationStartIndex(eqList, i);
                    i = lastIndex;
                    level--;
                }

            }

            if(level <= 0){
                return i;
            }
        }
        return -1; //DNE
    }
    public static Object parseString(String str){
        if(str.length() == 0){
            return null;
        }
        //Check it it's a terminator
        if(str.equals(")")){
            return new SyntaxTerminator();
        }
        if(str.trim().equals(",")){
            return new SyntaxSeperator();
        }
        //Next, check for numbers
        try{
            double num = Double.parseDouble(str);
            if(Math.floor(num) == num){ //Easy way to check if num is an int
                return new MathInteger((int) num);
            }
            //Nope, it's decimal. We hate decimal numbers, turn them into rational numbers.
            //Remove the decimal place and turn it into a fraction.
            //3.7 = 37/10
            //3.503 = 3503 / 1000
            //345.234 = 345234 / 1000

            int afterDecLength = str.length() - str.indexOf('.') - 1;

            int denom = (int) Math.pow(10, afterDecLength);
            int numer = (int) (num * denom);
            return new EquationBuilder().new RationalTempInfoHolder(new MathInteger(numer), new MathInteger(denom));
        } catch(NumberFormatException e){
            //IGNORE IT
        }
        //Maybe it's a generic. If it's an expression it'll look like _<genericName>
        if(str.charAt(0) == '_'){
            //There's a _ so it's a generic.
            // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
            //A generic looks like this:
            //_ (_ if named) name (_type)
            //The first underscore indicates a generic. The second says it's named. Then the name. Then, another udnerscore and the type (if it has one).
            int underScoreIndex = 1;
            StringBuilder nameBuild = new StringBuilder();
            boolean named = false;
            StringBuilder typeBuild = new StringBuilder();
            for(int i = 1; i<str.length(); i++){
                char current = str.charAt(i);
                if(current == '_'){
                    underScoreIndex++;
                    if(underScoreIndex == 2){
                        named = true;
                    }
                    continue;
                }
                switch(underScoreIndex){
                    case 1:
                        nameBuild.append(current);
                        underScoreIndex = 2;
                        break;
                    case 2:
                        nameBuild.append(current);
                        break;
                    case 3:
                        typeBuild.append(current);
                        break;
                    default:
                        //The fuck? Should only have 3 maximum underscores.
                }
            }
            if(nameBuild.length() == 0){
                return new GenericExpression();
            }
            if(typeBuild.length() == 0){
                return new GenericExpression(nameBuild.toString(), named, IdentificationType.VARIABLE);
            }
            return new GenericExpression(nameBuild.toString(), named, IdentificationType.valueOf(typeBuild.toString()));
        }
        //It's not a generic, so we don't need to base case sensitive at this point
        str = str.toUpperCase();
        //Check the list of mathematical operators.
        try{
            MathObject obj = new MathObject(MathOperator.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }

        //Check the abbriviations table
        AbbriviationData abbrData = (MathObjectAbbriviations.abbriviations.get(str));
        if(abbrData != null){ //A HashMap's .get() method returns null if there's no key.
            return abbrData;
        }
        throw new UncheckedIOException(new IOException("Unrecognized operator " + str + "!"));
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
    private static class SyntaxTerminator{
        @Override
        public boolean equals(Object n){
            return n instanceof SyntaxTerminator;
        }
    }
    private static class SyntaxSeperator{
        @Override
        public boolean equals(Object n){
            return n instanceof SyntaxSeperator;
        }
    }
}