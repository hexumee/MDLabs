package me.hexu.road.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Collections;

public class DatabaseManager {
    private final DatabaseHelper helper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void open() {
        database = helper.getWritableDatabase();
    }

    public void insert(String nickname, int score, long time) {
        ContentValues row = new ContentValues();
        row.put(DatabaseHelper.DATABASE_COLUMN_NICKNAME, nickname);
        row.put(DatabaseHelper.DATABASE_COLUMN_SCORE, score);
        row.put(DatabaseHelper.DATABASE_COLUMN_TIME, time);

        database.insert(DatabaseHelper.TABLE_NAME, null, row);
    }

    public ArrayList<DatabaseRow> getStatistics(String orderType) {
        ArrayList<DatabaseRow> result = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, orderType);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DATABASE_COLUMN_ID));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DATABASE_COLUMN_NICKNAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DATABASE_COLUMN_SCORE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.DATABASE_COLUMN_TIME));

            result.add(new DatabaseRow(id, nickname, score, time));
        }

        cursor.close();

        Collections.reverse(result);
        return result;
    }

    public void close() {
        helper.close();
    }
}
