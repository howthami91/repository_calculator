package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView textViewSolution;
    private TextView textViewResult;
    private String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSolution = findViewById(R.id.textViewSolution);
        textViewResult = findViewById(R.id.textViewResult);

        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] buttonIDs = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonAddition, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.buttonDot, R.id.button_Open_Bracket, R.id.button_Close_Bracket,
                R.id.buttonEquals, R.id.buttonC, R.id.button_AC
        };

        View.OnClickListener listener = view -> {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            switch (buttonText) {
                case "C":
                    if (!currentExpression.isEmpty()) {
                        currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    }
                    break;
                case "AC":
                    currentExpression = "";
                    textViewResult.setText("0");
                    break;
                case "=":
                    // No need to do anything for "=" button here, as the calculation is done dynamically.
                    break;
                default:
                    currentExpression += buttonText;
                    break;
            }

            textViewSolution.setText(currentExpression);

            // Evaluate the expression only if it's valid for evaluation.
            if (isExpressionValidForEvaluation(currentExpression)) {
                try {
                    String result = evaluateExpression(currentExpression);
                    textViewResult.setText(result);
                } catch (Exception e) {
                    textViewResult.setText("Error");
                }
            } else {
                textViewResult.setText("");  // Clear the result if the expression is incomplete.
            }
        };

        for (int id : buttonIDs) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private boolean isExpressionValidForEvaluation(String expression) {
        // Check if the expression ends with a digit or a closing bracket.
        if (expression.isEmpty()) return false;
        char lastChar = expression.charAt(expression.length() - 1);
        return Character.isDigit(lastChar) || lastChar == ')';
    }

    private String evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sbuf = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sbuf.append(expression.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(sbuf.toString()));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return String.valueOf(values.pop());
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        else return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            default: return 0;
        }
    }
}
