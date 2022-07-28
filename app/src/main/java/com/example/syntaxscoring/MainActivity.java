package com.example.syntaxscoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access text view where total syntax error score can be displayed
        TextView resultTextView = (TextView) findViewById(R.id.result);

        // Read the input resource file into input stream, which contain the sequence of data
        // arranged in line, meaning each line has return carriage
        Resources res = getResources();
        InputStream inputStream = res.openRawResource(R.raw.input1);

        // Organise identified lines in stream elements
        Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines();

        // Read per line from the stream
        lines.forEach(line -> System.out.println(line));
    }
}