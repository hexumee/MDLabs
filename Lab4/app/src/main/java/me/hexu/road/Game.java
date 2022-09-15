package me.hexu.road;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import me.hexu.road.database.DatabaseManager;

public class Game extends Application {
    private static Game instance;
    private SharedPreferences.Editor preferencesEditor;
    private String nickname;
    private DatabaseManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
        nickname = preferences.getString("nickname", "");

        manager = new DatabaseManager(this);
    }

    public static Game getInstance() {
        return instance;
    }

    public String getNickname() {
        return nickname;
    }

    public void applyNewNickname(String newNickname) {
        nickname = newNickname;

        preferencesEditor.putString("nickname", newNickname);
        preferencesEditor.apply();
    }

    public DatabaseManager getDatabaseManager() {
        return manager;
    }
}
