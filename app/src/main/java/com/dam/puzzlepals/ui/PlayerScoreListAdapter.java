package com.dam.puzzlepals.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.utils.TimeConverter;

import java.util.List;

public class PlayerScoreListAdapter extends BaseAdapter {

    private Context context;
    private List<Score> scoreArrayList;

    public PlayerScoreListAdapter(Context context, List<Score> scoreArrayList) {
        this.context = context;
        this.scoreArrayList = scoreArrayList;
    }

    @Override
    public int getCount() {
        return scoreArrayList.size();
    }

    @Override
    public Score getItem(int position) {
        return scoreArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Score scoreItem = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_player_score, null);
        TextView playerScoreDateTextView = convertView.findViewById(R.id.date_player_score);
        TextView playerScoreLevelTextView = convertView.findViewById(R.id.level_player_score);
        TextView playerScorePuzzleNumberTextView = convertView.findViewById(R.id.puzzle_number_player_score);
        TextView playerScoreScoreTextView = convertView.findViewById(R.id.score_player_score);

        String level = "";
        switch (scoreItem.getLevel()) {
            case EASY:
                level = context.getString(R.string.easy);
                break;
            case HARD:
                level = context.getString(R.string.hard);
                break;
            default:
                level = "-";
        }

        playerScoreDateTextView.setText(TimeConverter.convertDateToCompleteFormatDate(scoreItem.getDate()));
        playerScoreLevelTextView.setText(level);
        playerScorePuzzleNumberTextView.setText(scoreItem.getPuzzleNumber().toString());
        playerScoreScoreTextView.setText(TimeConverter.convertTimeMillisToReadableString(scoreItem.getScore()));

        return convertView;
    }

}
