/**
 * Description: Takes an input of a compound proposition with up to five variables including AND, OR, NOT and parentheses,
 * and will evaluate it given the truth values to return whether it is true or false.
 *
 * @programmer Manny Flores
 * @course COSC314, W'24
 * @project 1
 * @due (2-9-24)
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Project1 {
    private static Scanner keyboard;
    private static int variableNum = 1;

    private static String[] variables;

    private static ArrayList<String> validTerms = new ArrayList<>();
    private static ArrayList<String> validVars = new ArrayList<>();

    private static String[] infixExpression;

    public static void main(String[] args) {
        //populate valid terms, then set up the scanner to detect keyboard input.
        populateValidTerms();
        keyboard = new Scanner(System.in);

        System.out.println("Logical Expressions Evaluator");
        System.out.println("-----------------------------");

        evaluateVariableNum();
        variables = new String[variableNum];
        setVariableValues();

        System.out.println("Enter the logical expression:");
        while(true){
            String expression = keyboard.nextLine();
            if(checkExpression(expression)){
                break;
            }else{
                System.out.println("Invalid expression. Enter again.");
            }
        }

        //evaluates the expression
        System.out.println(evaluateExpression());
    }

    /**
     *  Loop to evaluate the input number for deciding how many variables you want to have. If you input a non-integer
     *  value, it will discard it and ask again until you do.
     *
     *  @return none
     */
    public static void evaluateVariableNum(){
        while(true){
            System.out.print("Enter the number of variables in the expression: ");
            String input = keyboard.nextLine();

            //check to see if the input was an integer. If it is, clamp it between 1 and 5 and break loop.
            if(input != null){
                try{
                    variableNum = Integer.parseInt(input);
                    if(variableNum < 1){
                        System.out.println("Minimum of 1 variable. Variable count set to 1.");
                        variableNum = 1;
                    }else if(variableNum > 5){
                        System.out.println("Maximum of 5 variables. Variable count set to 5.");
                        variableNum = 5;
                    }
                    break;
                }catch(NumberFormatException e){
                    System.out.println("Please input an integer.");
                }
            }
        }
    }

    /**
     *  After determining how many variables the user wants to have, the function will loop that many times and assign
     *  lowercase letters a-e to them, asking the user to fill out true or false values for each.
     *
     *  @return none
     */
        public static void setVariableValues(){
        System.out.println("Enter the values of variables - true or false:");
        for(int i = 0; i < variableNum; i++){

            //gets an ASCII character starting at lowercase a. This will automatically go up as the number of variables increases.
            char variableName = (char) (i + 97);

            //include our new variable name in the list of valid terms. This is done here instead of the population stage
            //so that the variable "d" will not be valid if there are 3 or less variables, but will be valid if there are
            //4 or more.
            validVars.add(String.valueOf(variableName));

            System.out.print(variableName + ": ");
            String input = keyboard.nextLine();
            if(input.equals("true")){
                variables[i] = "true";
            }else if(input.equals("false")){
                variables[i] = "false";
            }else{
                System.out.println("Invalid value. Enter true of false.");
                i--;
            }
        }
    }

    /**
     *  Populates the valid term list with all the terms we need to compare against what our user inputs. These will be
     *  put in the order of operations so we can compare them when evaluating the expression and do
     *  calculations in the correct order
     *
     *  @return none
     */
    public static void populateValidTerms(){
        validTerms.add("~");
        validTerms.add("/\\");
        validTerms.add("\\/");
        //validTerms.add("->");
        //validTerms.add("<->");
        validTerms.add("(");
        validTerms.add(")");
    }

    //prepares the expression to be split into multiple individual parts, then splits it up and checks each part to see if
    //everything is valid. Will also replace variables with "true" or "false" dependent on what values were assigned.

    /**
     *  Prepares the expression to be split into multiple individual terms and variables by splitting everything with spaces.
     *  After which, it checks each part to see if everything is valid, and replaces variables with "true" or "false"
     *  dependent on what values they are assigned. Returns true if the expression was valid, and false if not.
     *
     *  @param expression The infix expression the user has already input
     *  @return boolean
     */
    public static boolean checkExpression(String expression){
        //Add a space after each ~ and each (, and before each ).
        //This will allow us to more easily separate and parse each term later.
        for(int i = 0; i < expression.length(); i++){
            char current = expression.charAt(i);
            if(current == '~' || current == '('){
                expression = expression.substring(0, i + 1) + " " + expression.substring(i + 1);
                i++;
            }else if(current == ')'){
                expression = expression.substring(0, i) + " " + expression.substring(i);
                i++;
            }
        }


        String[] parsed = expression.split(" ");
        for (int i = 0; i < parsed.length; i++) {
            String s = parsed[i];
            //return false and try again if there is an invalid term in the expression.
            if(!validTerms.contains(s) && !validVars.contains(s)){
                return false;
            }

            //replace variables with T or F if they are true or false, we have stored these in the "variables" array, so
            //we can pull from there based on what character it is, getting the ASCII value.
            if(validVars.contains(s)){
                char varCharacter = s.charAt(0);
                int varPlace = (int)varCharacter - 97;
                parsed[i] = variables[varPlace];
            }

        }
        infixExpression = parsed;
        return true;
    }

    /**
     *  Takes the infix expression, then converts it to postfix using a stack. After that, it will go through the
     *  postfix expression and calculate the final result, then return that as a string. If anything goes wrong during
     *  the process, e.g. the expression is malformed, it will output an error message instead.
     *
     *  @return String
     */
    public static String evaluateExpression(){

        ArrayList<String> postfixExpression = new ArrayList<>();

        //Stack that stores all the operators of the infix expression when looping through it.
        Stack<String> operators = new Stack<>();

        //loops through infix expression, evaluates each variable and operator to convert to postfix
        for(String s : infixExpression){
            if(s.equals("true") || s.equals("false")){
                postfixExpression.add(s);
            }else if(validTerms.contains(s)){
                while(!operators.isEmpty()){
                    //if we run into a right parentheses, we will want to pop everything in the stack only until we run
                    //into a left parentheses.
                    if(s.equals(")")){
                        if(operators.peek().equals("(")){
                            operators.pop();
                            break;
                        }
                    }
                    //for every other operator, we only want to check to see if the operator on top of the stack is of
                    //a lower priority than it. If it is, there's no problem and we break the loop. Otherwise, we pop
                    //it from the stack and try again. Also, if the operator we want to add is a left parentheses,
                    //we just want to immediately break the loop without popping anything, as this parentheses is just
                    //an indicator of when to stop popping the stack when we eventually run into the right parentheses.

                    else if(s.equals("(") || validTerms.indexOf(operators.peek()) > validTerms.indexOf(s)){
                        break;
                    }

                    postfixExpression.add(operators.pop());
                }

                //We never want to push a right parentheses to the stack. When we run into one we only want to pop everything
                //from the stack until we run into a left parentheses.
                if(!s.equals(")")) {
                    operators.push(s);
                }
            }else{
                return "Sorry, something went wrong. Please try again.";
            }
        }

        //after evaluating everything, if the stack still has elements, pop each in order and add to the postfix expression
        while(!operators.isEmpty()){
            postfixExpression.add(operators.pop());
        }

        //We now create a new stack to push variables (T/F values) into.
        Stack<String> variableStack = new Stack<>();

        //loop through the postfixExpression to start calculating the final outcome.
        for(String s : postfixExpression){

            //if we run into a T/F value, push it to the variableStack.
            if(s.equals("true") || s.equals("false")){
                variableStack.push(s);
            }else if(!variableStack.isEmpty()){

                //If we run into an operator, we want to check what kind it is. Then we pop one (NOT) or two variables
                //from the stack, and calculate the outcome dependent on what operator was used, then push it back to the stack.
                switch (s) {
                    case "~":
                        variableStack.push(evalNot(variableStack.pop()));
                        break;
                    case "/\\":
                        variableStack.push(evalAnd(variableStack.pop(), variableStack.pop()));
                        break;
                    case "\\/":
                        variableStack.push(evalOr(variableStack.pop(), variableStack.pop()));
                        break;
                }
            }else{
                //If we run into an operator when the variableStack was empty, something must have gone wrong.
                return "Sorry, something went wrong. Expression may be malformed, please try again.";
            }
        }

        //If everything went well, we should have the answer as the only element in the variableStack, so we return it.
        return "The value of the given logical expression is " + variableStack.pop() + ".";
    }

    //Functions for evaluating logical operators, takes in strings, converts to boolean to do operations, then returns
    //a string back.

    /**
     *  Takes two strings which should always be "true" or "false", converts them into boolean values, does the and
     *  operation with both of them and returns the result as a string.
     *
     *  @param a The first string representing a boolean variable
     *  @param b The second string representing a boolean variable
     *  @return String
     */
    public static String evalAnd(String a, String b){
        boolean x = Boolean.valueOf(a);
        boolean y = Boolean.valueOf(b);
        return String.valueOf(x && y);
    }

    /**
     *  Takes two strings which should always be "true" or "false", converts them into boolean values, does the or
     *  operation with both of them and returns the result as a string.
     *
     *  @param a The first string representing a boolean variable
     *  @param b The second string representing a boolean variable
     *  @return String
     */
    public static String evalOr(String a, String b){
        boolean x = Boolean.valueOf(a);
        boolean y = Boolean.valueOf(b);
        return String.valueOf(x || y);
    }

    /**
     *  Takes one string which should always be "true" or "false", converts them into boolean values, does the not
     *  operation with it and returns the result as a string.
     *
     *  @param a The string representing a boolean variable
     *  @return String
     */
    public static String evalNot(String a){
        boolean x = Boolean.valueOf(a);
        return String.valueOf(!x);
    }

}