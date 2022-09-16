package me.hexu.road.views.leaderboard;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import me.hexu.road.R;
import me.hexu.road.database.DatabaseRow;

public class LeaderboardHolder extends RecyclerView.ViewHolder {
    private final TextView rowNumber;
    private final TextView rowNickname;
    private final TextView rowScore;
    private final TextView rowTime;

    public LeaderboardHolder(View itemView) {
        super(itemView);

        rowNumber = itemView.findViewById(R.id.row_number);
        rowNickname = itemView.findViewById(R.id.row_nickname);
        rowScore = itemView.findViewById(R.id.row_score);
        rowTime = itemView.findViewById(R.id.row_time);
    }

    public void bind(DatabaseRow row) {
        rowNumber.setText(String.valueOf(row.getId()));
        rowNickname.setText(row.getNickname());
        rowScore.setText(String.valueOf(row.getScore()));
        rowTime.setText(String.valueOf(row.getTime()));
    }
}
