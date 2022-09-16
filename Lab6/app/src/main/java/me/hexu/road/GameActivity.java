package me.hexu.road;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import me.hexu.road.database.DatabaseManager;
import me.hexu.road.database.remote.RemoteCallback;
import me.hexu.road.database.remote.RemoteDatabaseRequest;
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
            /*DatabaseManager manager = Game.getInstance().getDatabaseManager();
            manager.open();
            manager.insert(Game.getInstance().getNickname(), score, time);
            manager.close();*/
            RemoteDatabaseRequest.requestUrlPost(new RemoteCallback() {
                @Override
                public void onGetResponse(String result) { }

                @Override
                public void onPostResponse(String result) {
                    try {
                        JSONObject response = new JSONObject(result);

                        if (!response.getBoolean("success")) {
                            String reason = response.getString("reason");
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, Game.getInstance().getNickname(), score, time);

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), String.format(getResources().getConfiguration().locale, getResources().getString(R.string.game_over_template), score, time), Toast.LENGTH_LONG).show());
            finish();
        });
    }
}
