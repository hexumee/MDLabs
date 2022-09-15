package me.hexu.road.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "game.db";
    public static final String TABLE_NAME = "statistics";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_COLUMN_ID = "id";
    public static final String DATABASE_COLUMN_NICKNAME = "nickname";
    public static final String DATABASE_COLUMN_SCORE = "score";
    public static final String DATABASE_COLUMN_TIME = "game_time";

    public static final String DATABASE_QUERY_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                                                           DATABASE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                                                           DATABASE_COLUMN_NICKNAME + " TEXT," +
                                                           DATABASE_COLUMN_SCORE + " INTEGER," +
                                                           DATABASE_COLUMN_TIME + " INTEGER" +
                                                       ")";

    public static final String DATABASE_QUERY_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL(DATABASE_QUERY_DELETE);
        database.execSQL(DATABASE_QUERY_CREATE);
    }
}
