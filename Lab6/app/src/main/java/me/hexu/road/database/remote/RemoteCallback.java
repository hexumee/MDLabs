package me.hexu.road.database.remote;

public interface RemoteCallback {
    void onGetResponse(String result);
    void onPostResponse(String result);
}
