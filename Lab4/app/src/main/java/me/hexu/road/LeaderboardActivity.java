package me.hexu.road;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.hexu.road.database.DatabaseManager;
import me.hexu.road.views.leaderboard.LeaderboardAdapter;

public class LeaderboardActivity extends AppCompatActivity {
    private final DatabaseManager manager = Game.getInstance().getDatabaseManager();
    private final LeaderboardAdapter adapter = new LeaderboardAdapter();
    private boolean sortByTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_leaderboard);

        RecyclerView leaderboardList = findViewById(R.id.list);
        leaderboardList.setLayoutManager(new LinearLayoutManager(this));
        leaderboardList.setAdapter(adapter);

        manager.open();
        adapter.setItems(manager.getStatistics("score"));
        manager.close();

        findViewById(R.id.sort_switch).setOnClickListener(v -> {
            sortByTime = !sortByTime;
            manager.open();
            adapter.setItems(sortByTime ? manager.getStatistics("game_time") : manager.getStatistics("score"));
            manager.close();
        });

        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
