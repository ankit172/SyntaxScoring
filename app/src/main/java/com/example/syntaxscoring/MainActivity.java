package com.example.syntaxscoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    int totalSyntaxErrorScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access text view where total syntax error score can be displayed
        TextView resultTextView = (TextView) findViewById(R.id.result);

        // Read the input resource file into input stream, which contain the sequence of data
        // arranged in line, meaning each line has return carriage
        Resources res = getResources();
        InputStream inputStream = res.openRawResource(R.raw.input2);

        // Organise identified lines in stream elements
        Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines();

       /*
        lines stream contain all lines split with return carriage.
        Using Stack, which is last in first out (LIFO) behavior.
        Go through each line and each character.
        If the character is opening character, add to stack the complement character,
        Otherwise if it is closing character, compare with complement from stack top element (stack
        pop) which removes the pair.
        If there is corrupt character, the logic will calculate the syntax error score.
         */
        lines.forEach(line -> {
            Stack<Character> stack = new Stack<>();
            for (int c : line.chars().toArray()) {
                switch (c) {
                    case '(':
                        stack.add(')');
                        break;
                    case '[':
                        stack.add(']');
                        break;
                    case '{':
                        stack.add('}');
                        break;
                    case '<':
                        stack.add('>');
                        break;
                    default: {
                        //In case character encounter is closing character
                        Character head = stack.pop();
                        if (head == null) {
                            return;
                        }
                        if (head != c) {
                            totalSyntaxErrorScore += getScoreBasedOnCharacter((char) c);
                            return;
                        }
                    }
                }
            }
        });
        String result = "Total Syntax error score: " + totalSyntaxErrorScore;
        System.out.println(result);
        resultTextView.setText(result);
    }

    /**
     * Function to calculate Syntax error score based on Illegal characters
     * which is defined by following points:
     * ): 3 points.
     * ]: 57 points.
     * }: 1197 points.
     * >: 25137 points.
     * @param c is the character whose error score is requested.
     * @return return error score, in case invalid character is found return 0.
     */
    private int getScoreBasedOnCharacter(char c) {
        switch (c) {
            case ')':
                return 3;
            case ']':
                return 57;
            case '}':
                return 1197;
            case '>':
                return 25137;
        }
        return 0;
    }
}