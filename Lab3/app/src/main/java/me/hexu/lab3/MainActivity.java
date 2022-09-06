package me.hexu.lab3;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText leftXGraphBound;
    private EditText rightXGraphBound;
    private EditText upperYGraphBound;
    private EditText lowerYGraphBound;
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        graphView = findViewById(R.id.graph);
        leftXGraphBound = findViewById(R.id.left_x_graph_bound);
        rightXGraphBound = findViewById(R.id.right_x_graph_bound);
        upperYGraphBound = findViewById(R.id.upper_y_graph_bound);
        lowerYGraphBound = findViewById(R.id.lower_y_graph_bound);

        findViewById(R.id.build_plot).setOnClickListener(v -> {
            if (leftXGraphBound.getText().toString().isEmpty()) {
                leftXGraphBound.setError("Значение не может быть пустым!");
                return;
            }

            if (rightXGraphBound.getText().toString().isEmpty()) {
                rightXGraphBound.setError("Значение не может быть пустым!");
                return;
            }

            if (Float.parseFloat(leftXGraphBound.getText().toString()) > Float.parseFloat(rightXGraphBound.getText().toString())) {
                Toast.makeText(this, "От X должно быть меньше До X", Toast.LENGTH_SHORT).show();
                return;
            }

            if (upperYGraphBound.getText().toString().isEmpty()) {
                upperYGraphBound.setError("Значение не может быть пустым!");
                return;
            }

            if (lowerYGraphBound.getText().toString().isEmpty()) {
                lowerYGraphBound.setError("Значение не может быть пустым!");
                return;
            }

            if (Float.parseFloat(upperYGraphBound.getText().toString()) > Float.parseFloat(lowerYGraphBound.getText().toString())) {
                Toast.makeText(this, "От Y должно быть меньше До Y", Toast.LENGTH_SHORT).show();
                return;
            }

            graphView.setBounds(Float.parseFloat(leftXGraphBound.getText().toString()),
                                Float.parseFloat(rightXGraphBound.getText().toString()),
                                Float.parseFloat(upperYGraphBound.getText().toString()),
                                Float.parseFloat(lowerYGraphBound.getText().toString())
            );

            graphView.registerCallback((newX0, newX1, newY0, newY1) -> {
                leftXGraphBound.setText(String.valueOf(newX0));
                rightXGraphBound.setText(String.valueOf(newX1));
                upperYGraphBound.setText(String.valueOf(newY0));
                lowerYGraphBound.setText(String.valueOf(newY1));
            });
        });
    }
}