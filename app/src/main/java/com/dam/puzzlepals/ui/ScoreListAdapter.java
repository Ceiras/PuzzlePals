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

import java.util.ArrayList;

public class ScoreListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Score> scoreArrayList;

    public ScoreListAdapter(Context context, ArrayList<Score> scoreArrayList) {
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
        return scoreArrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Score scoreItem = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_score, null);
        TextView scoreDateTextView = convertView.findViewById(R.id.date_top_score);
        TextView scoreTimeTextView = convertView.findViewById(R.id.time_top_score);

        scoreDateTextView.setText(TimeConverter.convertDateToCompleteFormatDate(scoreItem.getDate()));
        scoreTimeTextView.setText(TimeConverter.convertTimeMillisToReadableString(scoreItem.getScore()));

        return convertView;
    }

}
