package me.hexu.road.database;

public class DatabaseRow {
    private final int id;
    private final String nickname;
    private final int score;
    private final long time;

    public DatabaseRow(int id, String nickname, int score, long time) {
        this.id = id;
        this.nickname = nickname;
        this.score = score;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }
}
