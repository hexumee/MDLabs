package me.hexu.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final int OP_TYPE_ADD = 1;
    private final int OP_TYPE_SUB = 2;
    private final int OP_TYPE_MUL = 3;
    private final int OP_TYPE_DIV = 4;

    private EditText firstField;
    private EditText secondField;
    private TextView errView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstField = findViewById(R.id.first_number);
        secondField = findViewById(R.id.second_number);
        errView = findViewById(R.id.err);

        Button addButton = findViewById(R.id.add);
        Button subtractButton = findViewById(R.id.subtract);
        Button multiplyButton = findViewById(R.id.multiply);
        Button divideButton = findViewById(R.id.divide);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        addButton.setOnClickListener(v -> this.calculate(OP_TYPE_ADD));
        subtractButton.setOnClickListener(v -> this.calculate(OP_TYPE_SUB));
        multiplyButton.setOnClickListener(v -> this.calculate(OP_TYPE_MUL));
        divideButton.setOnClickListener(v -> this.calculate(OP_TYPE_DIV));
    }

    private void calculate(int opType) {
        if (firstField.getText().toString().isEmpty() || secondField.getText().toString().isEmpty()) {
            errView.setText(getResources().getString(R.string.empty_field));
            errView.setVisibility(View.VISIBLE);
        } else {
            float x = Float.parseFloat(firstField.getText().toString());
            float y = Float.parseFloat(secondField.getText().toString());

            switch (opType) {
                case OP_TYPE_ADD:
                    startActivity(new Intent(getApplicationContext(), ResultActivity.class).putExtra("result", x+y));
                    break;
                case OP_TYPE_SUB:
                    startActivity(new Intent(getApplicationContext(), ResultActivity.class).putExtra("result", x-y));
                    break;
                case OP_TYPE_MUL:
                    startActivity(new Intent(getApplicationContext(), ResultActivity.class).putExtra("result", x*y));
                    break;
                case OP_TYPE_DIV:
                    if (y != 0) {
                        startActivity(new Intent(getApplicationContext(), ResultActivity.class).putExtra("result", x/y));
                    } else {
                        startActivity(new Intent(getApplicationContext(), ResultActivity.class).putExtra("err", getString(R.string.inf)));
                    }
                    break;
                default:
                    break;
            }

            errView.setVisibility(View.GONE);
        }
    }
}
