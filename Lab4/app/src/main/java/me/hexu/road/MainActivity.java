package me.hexu.road;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_main);
        nickname = findViewById(R.id.nickname);
        nickname.setText(Game.getInstance().getNickname());

        findViewById(R.id.start).setOnClickListener(v -> {
            if (nickname.getText().toString().isEmpty()) {
                nickname.setError(getString(R.string.err_empty_nickname));
                return;
            }

            if (!Game.getInstance().getNickname().equals(nickname.getText().toString())) {
                Game.getInstance().applyNewNickname(nickname.getText().toString());
            }

            startActivity(new Intent(getApplicationContext(), GameActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.leaderboard).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LeaderboardActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
