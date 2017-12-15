package rpnUtil;

import java.util.Stack;
import java.util.Arrays;

/**
 * Reverse Polish Notation (RPN) class
 *
 * @author marco
 * @version 20171215
 */

public class RPN {

    private String infixExpression;
    private String suffixExpression;
    private double value;

    /**
     * Just creates the RPN object to access its methods
     */
    public RPN() {
    }

    /**
     * Creates the RPN object with the given infix expression, calculates the suffix notation and evaluates it
     *
     * @param infixExpression
     */
    public RPN(String infixExpression) {
        this.infixExpression = setWhiteSpaces(infixExpression);
        this.suffixExpression = infixToSuffix(setWhiteSpaces(searchForUnary(this.infixExpression)));
        this.value = evaluate(this.suffixExpression);
    }

    /**
     * Gets the infix expression and converts it in the format the algorithm needs
     *
     * @param infixExpression
     * @return the right format expression
     */
    public String setWhiteSpaces(String infixExpression) {
        String output = "";
        infixExpression = infixExpression.replace(" ", "");
        String[] operators = new String[]{"-", "+", "*", "/", "^", "~", "(", ")"};

        for (char c : infixExpression.toCharArray()) {
            if (Arrays.stream(operators).anyMatch(("" + c)::equals)) output += " " + c + " ";
            else output += c;
        }

        if (output.charAt(0) == ' ') output = output.substring(1, output.length());
        if (output.charAt(output.length() - 1) == ' ') output = output.substring(0, output.length() - 1);

        return output.replace("  ", " ");
    }

    /**
     * Checks for the '-' unary operator and replaces it with '~'
     *
     * @param infixExpression
     * @return the modified string
     */
    public String searchForUnary(String infixExpression) {
        StringBuilder elaboratingString = new StringBuilder(infixExpression.replace(" ", ""));
        String[] operators = new String[]{"-", "+", "*", "/", "^", "~", "(", ")"};
        for (int i = 0; i < elaboratingString.length(); i++) {
            if ((elaboratingString.charAt(i) == '-')
                    && ((i == 0) || (Arrays.stream(operators).anyMatch(("" + elaboratingString.charAt(i - 1))::equals)))
                    ) {
                elaboratingString.setCharAt(i, '~');
            }
        }
        return elaboratingString.toString();
    }

    /**
     * Coverts an infix expression to a suffix expression
     *
     * @param infixExpression
     * @return the converted expression
     */
    public String infixToSuffix(String infixExpression) {

        Stack<String> stack = new Stack();
        String[] splitString = infixExpression.split(" ");
        String output = new String();
        String[] operators = new String[]{"-", "+", "*", "/", "~", "^"};
        Integer[] precedence = new Integer[]{2, 2, 3, 3, 3, 5};
        String[] leftAssociation = new String[]{"-", "+", "*", "/"};

        for (String token : splitString) { // Algorithm taken from https://en.wikipedia.org/wiki/Shunting-yard_algorithm
            if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.peek().equals("(")) {
                    output += stack.pop() + " ";
                }
                stack.pop();
            } else if (!Arrays.stream(operators).anyMatch(token::equals)) {
                output += token + " ";
            } else if (Arrays.stream(operators).anyMatch(token::equals)) {
                while ((!stack.empty()) && (!stack.peek().equals("(")) &&
                        ((precedence[Arrays.asList(operators).indexOf(stack.peek())] >
                                precedence[Arrays.asList(operators).indexOf(token)])
                                || ((precedence[Arrays.asList(operators).indexOf(stack.peek())] ==
                                precedence[Arrays.asList(operators).indexOf(token)]) &&
                                (Arrays.stream(leftAssociation).anyMatch(token::equals))))
                        ) {
                    output += stack.pop() + " ";
                }
                stack.push(token);
            } else {
                throw new IllegalArgumentException("Token not recognised!");
            }
        }
        while (!stack.empty()) {
            output += stack.pop() + " ";
        }
        return output.substring(0, output.length() - 1); // Removes the last whitespace
    }

    /**
     * Evaluates a suffix expression
     *
     * @param suffixExpression
     * @return the value of the expression
     */
    public double evaluate(String suffixExpression) {

        Stack<Double> stack = new Stack();
        String[] splitString = suffixExpression.split(" ");
        String[] operators = new String[]{"-", "+", "*", "/", "^", "~"};

        for (String s : splitString) { // Algorithm taken from https://en.wikipedia.org/wiki/Reverse_Polish_notation
            if (!Arrays.stream(operators).anyMatch(s::equals)) {
                stack.push(Double.parseDouble(s));
            } else if (s.equals("~")) {
                stack.push(stack.pop() * -1);
            } else {
                Double secondOperand = stack.pop();
                Double firstOperand = stack.pop();
                switch (s) {
                    case "+":
                        stack.push(firstOperand + secondOperand);
                        break;
                    case "-":
                        stack.push(firstOperand - secondOperand);
                        break;
                    case "*":
                        stack.push(firstOperand * secondOperand);
                        break;
                    case "/":
                        stack.push(firstOperand / secondOperand);
                        break;
                    case "^":
                        stack.push(Math.pow(firstOperand, secondOperand));
                        break;
                }
            }

        }
        return stack.pop();
    }

    @Override
    public String toString() {
        return "RPN{" +
                "infixExpression='" + infixExpression + '\'' +
                ", suffixExpression='" + suffixExpression + '\'' +
                ", value=" + value +
                '}';
    }
}
