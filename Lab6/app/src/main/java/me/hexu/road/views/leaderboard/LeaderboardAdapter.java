package me.hexu.road.views.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import me.hexu.road.R;
import me.hexu.road.database.DatabaseRow;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardHolder> {
    private final ArrayList<DatabaseRow> items = new ArrayList<>();

    @NonNull
    @Override
    public LeaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row, parent, false);

        return new LeaderboardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<DatabaseRow> newItems) {
        int currentSize = items.size();

        items.clear();
        notifyItemRangeRemoved(0, currentSize);
        items.addAll(newItems);
        notifyItemRangeInserted(0, newItems.size());
    }
}
