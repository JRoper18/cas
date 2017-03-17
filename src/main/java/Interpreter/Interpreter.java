package Interpreter;

import CAS.Equation;

import java.util.HashMap;

/**
 * Created by Ulysses Howard Smith on 3/17/2017.
 */
public class Interpreter {
    /*Each line contains a statement. A statement's strcuture looks like this:
    <BASIC_COMMAND || META_COMMAND> <ARGS>
    BasicCommand is a command from a set of commands that the programmer has to choose from. Some examples:
    DEFINE, IF, FOR, WHILE, etc.
    Meanwhile, a Meta command is just one of the MULTIPLE meta commands that we defined in our equationsubdatabase.
    After the name of the command we have space-seperated arguments which are fed into that command. And arg can be either:
    1. An equation, or
    2. An evaluatable statement.

    */

    HashMap<String, Equation> vars;

}
