package me.hexu.road;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import me.hexu.road.database.DatabaseManager;
import me.hexu.road.views.GameView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_game);

        ((GameView) findViewById(R.id.game)).registerCallback((score, time) -> {
            DatabaseManager manager = Game.getInstance().getDatabaseManager();
            manager.open();
            manager.insert(Game.getInstance().getNickname(), score, time);
            manager.close();

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), String.format(getResources().getConfiguration().locale, getResources().getString(R.string.game_over_template), score, time), Toast.LENGTH_LONG).show());
            finish();
        });
    }
}
