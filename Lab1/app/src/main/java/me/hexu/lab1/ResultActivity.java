package me.hexu.lab1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setTitle(getResources().getString(R.string.result_text));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView resultText = findViewById(R.id.result_text);
        float res = getIntent().getFloatExtra("result", 0f);

        if (res != (int) res) {
            StringBuilder sb = new StringBuilder(String.format(getResources().getString(R.string.result_float_template), res));

            for (int i = sb.length()-1; i > 0; i--) {
                if (sb.charAt(i) == '0') {
                    sb.deleteCharAt(i);
                } else {
                    break;
                }
            }

            resultText.setText(sb);
        } else {
            resultText.setText(String.format(getResources().getString(R.string.result_int_template), (int)res));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}