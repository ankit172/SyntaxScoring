package com.example.syntaxscoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    int totalSyntaxErrorScore = 0;
    List<Long> totalCompletionStringScore = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access text view where results can be displayed
        TextView result1TextView = findViewById(R.id.result1);
        TextView result2TextView = findViewById(R.id.result2);

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
                        // In case character encounter is closing character
                        // Return in order not to execute for second part which is valid only for
                        // incomplete lines and not for corrupt lines
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

            // Create reverse of stack since first closing character will be at bottom of stack
            Stack<Character> newStack = new Stack<>();
            for (int i = 0; i < stack.size(); i++) {
                newStack.push(stack.get(stack.size() - i - 1));
            }
            // Defined as atomic to access within lambda function
            AtomicLong totalScore = new AtomicLong();
            newStack.forEach(c -> {
                    totalScore.updateAndGet(v -> v * 5);
                    switch (c) {
                        case ')':
                            totalScore.updateAndGet(v -> v + 1);
                            break;
                        case ']':
                            totalScore.updateAndGet(v -> v + 2);
                            break;
                        case '}':
                            totalScore.updateAndGet(v -> v + 3);
                            break;
                        case '>':
                            totalScore.updateAndGet(v -> v + 4);
                            break;
                        default:
                    }
                });
            totalCompletionStringScore.add(totalScore.get());
        });
        String result1 = "Total syntax error score: " + totalSyntaxErrorScore;
        System.out.println(result1);
        result1TextView.setText(result1);

        // Sorted in ascending order and get middle total score
        long[] scoreList = totalCompletionStringScore.stream().mapToLong(Long::longValue).sorted().toArray();
        long totalScore = scoreList[scoreList.length / 2];

        String result2 = "Total completion string score: " + totalScore;
        System.out.println(result2);
        result2TextView.setText(result2);
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