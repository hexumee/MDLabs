package me.hexu.road;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import me.hexu.road.database.DatabaseManager;
import me.hexu.road.database.DatabaseRow;
import me.hexu.road.database.remote.RemoteCallback;
import me.hexu.road.database.remote.RemoteDatabaseRequest;
import me.hexu.road.views.leaderboard.LeaderboardAdapter;

public class LeaderboardActivity extends AppCompatActivity implements RemoteCallback {
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

        /*manager.open();
        adapter.setItems(manager.getStatistics("score"));
        manager.close();*/

        RemoteDatabaseRequest.requestUrlGet(this, sortByTime ? "time" : "score");

        findViewById(R.id.sort_switch).setOnClickListener(v -> {
            sortByTime = !sortByTime;
            findViewById(R.id.work_in_progress).setVisibility(View.VISIBLE);
            RemoteDatabaseRequest.requestUrlGet(this, sortByTime ? "time" : "score");
            /*manager.open();
            adapter.setItems(sortByTime ? manager.getStatistics("game_time") : manager.getStatistics("score"));
            manager.close();*/
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

    @Override
    public void onGetResponse(String result) {
        ArrayList<DatabaseRow> leaderboardRemote = new ArrayList<>();
        ArrayList<String> rawData = new ArrayList<>(Arrays.asList(result.split(";")));

        for (String row : rawData) {
            String[] columns = row.split(",");
            leaderboardRemote.add(new DatabaseRow(Integer.parseInt(columns[0]), columns[1], Integer.parseInt(columns[2]), Long.parseLong(columns[3])));
        }

        runOnUiThread(() -> {
            adapter.setItems(leaderboardRemote);
            findViewById(R.id.work_in_progress).setVisibility(View.GONE);
        });
    }

    @Override
    public void onPostResponse(String result) {
        // Not implemented
    }
}
