package me.hexu.lab2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Locale.setDefault(App.localeRu);
            Configuration config = new Configuration();
            config.locale = App.localeRu;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Locale.setDefault(App.localeEn);
            Configuration configuration = new Configuration();
            configuration.locale = App.localeEn;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_main);

        ListView list = findViewById(R.id.list);
        Button selectAll = findViewById(R.id.select_all);
        Button unselectAll = findViewById(R.id.unselect_all);
        Button addNewElem = findViewById(R.id.add_new);
        Button outElements = findViewById(R.id.out_selection);
        TextView elemName = findViewById(R.id.name_input);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, App.getItems());
        list.setAdapter(adapter);

        selectAll.setOnClickListener(v -> {
            for (int i = 0; i < list.getCount(); i++) {
                list.setItemChecked(i, true);
            }
        });

        unselectAll.setOnClickListener(v -> {
            for (int i = 0; i < list.getCount(); i++) {
                list.setItemChecked(i, false);
            }
        });

        addNewElem.setOnClickListener(v -> {
            if (!elemName.getText().toString().isEmpty()) {
                adapter.add(elemName.getText().toString());
            } else {
                Toast.makeText(this, getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
            }
        });

        outElements.setOnClickListener(v -> {
            SparseBooleanArray sparseBooleanArray = list.getCheckedItemPositions();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < list.getCount(); i++) {
                if (sparseBooleanArray.get(i)) {
                    sb.append(list.getItemAtPosition(i).toString()).append(" ");
                }
            }

            Toast.makeText(this, sb, Toast.LENGTH_SHORT).show();
        });
    }
}
