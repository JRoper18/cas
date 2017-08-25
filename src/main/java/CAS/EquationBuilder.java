package CAS;

import CAS.EquationObjects.*;
import Identification.IdentificationType;
import Simplification.Simplifier;
import Simplification.SimplifierObjective;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class EquationBuilder{
    public static final String regex = generateStringSplitRegex();
    public static Equation makeEquation(String str, int autoSimplifyLevel){
        if(autoSimplifyLevel == 0){
            return new Equation(makeEquationTree(str));
        }
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
                    return Simplifier.directSimplify(processedEquation, SimplifierObjective.REMOVE_META);
                case 2: //Autosimplify this
                    processedEquation = Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.SIMPLIFY_RATIONAL_EXPRESSION);
                    processedEquation = Simplifier.orderEquation(processedEquation);
                    return Simplifier.orderEquation(Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.AUTOSIMPLIFY));
                default:
                    return processedEquation;
            }
        }
    }

    /**
     * Takes an input string that can include both infix and prefix notation and converts it to solely prefix notation.
     * @param str The equation input
     * @return A correctly formatted prefix string, but with arguements reversed.
     */
    public static String infixToPrefix(String str){
        String newStr = str.trim().replaceAll("((\\s|^)\\-(\\S))", "@$3"); //The @ will be a negative sign.
        newStr = newStr.trim().replaceAll("((\\(|\\,)\\-(\\S))", "$2@$3"); //Might be a ( or , before a negative sign
        newStr = "" + newStr.trim().replaceAll(" ", "") + "";
        List<String> tokens = Arrays.asList(newStr.split(regex));
        if(tokens.size() == 1){
            if(tokens.get(0).charAt(0) == '@'){
                return ("-" + tokens.get(0).substring(1, tokens.get(0).length()));
            }
            return (tokens.get(0));
        }
        List<String> newList = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for(String token: tokens) {
            token = token.trim();
            if((token.charAt(0) == '_' && !token.contains("GENERICFUNCTION"))|| Character.isDigit(token.charAt(0)) || (token.charAt(0)== '@' && Character.isDigit(token.charAt(1)))){ //Variable or number
                if(token.charAt(0) == '@'){
                    newList.add("-" + token.substring(1, token.length()));
                }
                else{
                    newList.add(token);
                }
            }
            else if(token.charAt(token.length()-1) == '('){ //It's either a prefix function or a plain open paren
                if(token.length() == 1){ //Open paren
                    newList.add("(");
                }
                else { //Function
                    stack.push(token);
                    newList.add("(");
                }
            }
            else{
                if(token.equals(")")){
                    while(!stack.isEmpty() && stack.peek().indexOf("(") == -1){
                        backtrackParen(newList);
                        String pop = stack.pop();
                        if(pop.length() != 1){ //Function
                            newList.add(pop.substring(0, pop.length()-1));
                         }
                        else{
                            newList.add(pop);
                        }
                    }
                    if(!stack.isEmpty() && stack.peek().indexOf("(") != -1){
                        newList.add(")");
                        String pop = stack.pop();
                        if(pop.length() != 1){ //Function
                            newList.add(pop.substring(0, pop.length()-1));
                        }
                        else{
                            newList.add(pop);
                        }
                    }
                }
                else if(token.equals(",")){
                    while(!stack.isEmpty() && stack.peek().indexOf("(") == -1){
                        backtrackParen(newList);
                        String pop = stack.pop();
                        if(pop.length() != 1){ //Function
                            newList.add(pop.substring(0, pop.length()-1));
                        }
                        else{
                            newList.add(pop);
                        }
                    }
                }
                else{ //It's an infix operator
                    stack.push(token);
                }
            }
        }
        while(!stack.isEmpty()){
            backtrackParen(newList);
            String pop = stack.pop();
            if(pop.length() != 1){ //Function
                newList.add(pop.substring(0, pop.length()-1));
            }
            else{
                newList.add(pop);
            }
        }
        Collections.reverse(newList);
        StringBuilder unprocessed = new StringBuilder(String.join(" ", newList));
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
            }
        }
        String parenSwitched = unprocessed.toString();
        parenSwitched = parenSwitched.replaceAll("\\(", "");
        return parenSwitched;
    }
    private static void backtrackParen(List<String> tokens){
        if(tokens.size() == 0){
            return;
        }
        if(tokens.size() == 1 || !tokens.get(tokens.size() -1).equals(")")){
            tokens.add(0, "(");
            tokens.add(")");
            return;
        }
        int level = 0;
        for(int i = tokens.size() - 1; i>=0; i--) {
            if (tokens.get(i).equals("(")) {
                level--;
            }
            if (tokens.get(i).equals(")")) {
                level++;
            }
            if (level == 0) {
                tokens.add(tokens.size() - i, "(");
                tokens.add(")");
                return;
            }
        }
    }
    /**
     * Creates a regex expression that splits an input string into seperate equation parts to be processed
     * @return A Regex string that will break up any equation string input into it's parts
     */
    private static String generateStringSplitRegex(){
        String innerBuild = "([\\\\)])"; //All equation must be split up with )
        final String specialChars = "\\.[]{}()*+-?^$|/"; //Special characters that require a \\ in order to be put correctly into a string or regex
        final String unaryPrefixChars = "-!";
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
                    throw new UncheckedIOException(new IOException("You have the wrong number of arguments for the operator: " + selected.data + ". It takes " + operator.getArgs() + " arguments, but you put in too little with the equation: " + equationStr));
                }
                selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren() - 1);
            } else if (equationObject instanceof RationalTempInfoHolder) {  //It's not syntax. Is it a temporary info holder?
                selected.data = new MathObject(MathOperator.FRACTION);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).numer); //Order is switched
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).denom);
                Equation newFrac = Simplifier.simplifyWithMetaFunction(new Equation(selected), MathOperator.SIMPLIFY_RATIONAL_FRACTION);
                if(newFrac.getRoot().getOperator() == MathOperator.FRACTION){ //It didn't turn it into a whole number, whose order isn't switched
                    selected.replaceWith(newFrac.tree);
                }
                else{
                    //Else it's a whole number when simplified, meaning it's reversed should be something like 1/10 instead of 10.
                    Tree<MathObject> fractionReplace = new Tree<>(new MathObject(MathOperator.FRACTION));
                    if(newFrac.isType(IdentificationType.NEGATIVE_CONSTANT)){
                        //Negative numbers on top.
                        fractionReplace.addChildWithData(new MathInteger(((MathInteger) newFrac.getRoot()).num.abs()));
                        fractionReplace.addChildWithData(new MathInteger(-1));
                    }
                    else{
                        fractionReplace.addChildWithData(newFrac.getRoot());
                        fractionReplace.addChildWithData(new MathInteger(1));
                    }
                    selected.replaceWith(fractionReplace);
                }
                if(!selected.isRoot()){
                    selected.getParent().addEmptyChild();
                    selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren()-1);
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
        //Now remember to reverse the order, because our infix to prefix returns operators in the wrong order.
        tree.reverseChildrenOrder();
        //Also, we're going to have MINUS(...) whever someone types a negative. Replace a minus signs with negative numbers if possible.
        return tree;
    }
    private static List<Object> preProcess(String equationStr){
        String newStr = infixToPrefix(equationStr);

        String[] tokens = newStr.trim().split(" ");
        List<Object> equationObjectList = new ArrayList<>();
        for(int i = 0; i<tokens.length; i++){
            if(tokens[i].trim().isEmpty()){
                continue;
            }
            Object next = parseString(tokens[i]);
            if(next != null){
                equationObjectList.add(next);
            }
        }
        //Now set all abbrivations to operators
        List<Object> newList = new ArrayList<>();
        for(int i = 0; i<equationObjectList.size(); i++){
            Object current = equationObjectList.get(i);
            if(current instanceof AbbriviationData){
                newList.add(((AbbriviationData) current).op);
            }
            else{
                newList.add(current);
            }
        }
        return newList;
    }
    public static Object parseString(String str){
        if(str.charAt(0) == '@') {
            str = "-" + str.substring(1, str.length());
        }
        if(str.length() == 0){
            return null;
        }
        //Check it it's a terminator
        if(str.equals(")")){
            return new SyntaxTerminator();
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
            if(typeBuild.toString().equals("GENERICFUNCTION")){
                return new GenericFunction(nameBuild.toString());
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
            //Don't forget, our infix to prefix algorthm reverses EVERYTHING
            this.numer = denom;
            this.denom = numer;
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
        @Override
        public String toString(){
            return ")";
        }
    }
}