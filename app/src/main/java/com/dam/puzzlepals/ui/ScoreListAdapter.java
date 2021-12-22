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

public class ScoreListAdapter extends BaseAdapter {

    private Context context;
    private List<Score> scoreArrayList;

    public ScoreListAdapter(Context context, List<Score> scoreArrayList) {
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

        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_score, null);
        TextView scoreDateTextView = convertView.findViewById(R.id.date_top_score);
        TextView scorePlayerTextView = convertView.findViewById(R.id.player_top_score);
        TextView scoreLevelTextView = convertView.findViewById(R.id.level_top_score);
        TextView scorePuzzleNumberTextView = convertView.findViewById(R.id.puzzle_number_top_score);
        TextView scoreScoreTextView = convertView.findViewById(R.id.score_top_score);

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

        scoreDateTextView.setText(TimeConverter.convertDateToCompleteFormatDate(scoreItem.getDate()));
        scorePlayerTextView.setText(scoreItem.getPlayer());
        scoreLevelTextView.setText(level);
        scorePuzzleNumberTextView.setText(scoreItem.getPuzzleNumber().toString());
        scoreScoreTextView.setText(TimeConverter.convertTimeMillisToReadableString(scoreItem.getScore()));

        return convertView;
    }

}
