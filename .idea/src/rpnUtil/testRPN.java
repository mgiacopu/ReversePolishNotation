package rpnUtil;

public class testRPN {
    public static void main(String[] args) {
        RPN rpn = new RPN("(-3+3)*4");
        System.out.println(rpn);
    }
}