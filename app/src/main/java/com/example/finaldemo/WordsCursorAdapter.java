package com.example.finaldemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.provider.UserDictionary;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.database.CursorWindowCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaldemo.data.WordsContract;



public class WordsCursorAdapter extends RecyclerView.Adapter<WordsCursorAdapter.ViewHolder> {

    public Cursor mCursor;
    private Context mContext;

    public WordsCursorAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        int idIndex = mCursor.getColumnIndex(WordsContract.WordsEntry._ID);
        int levelIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_LEVEL);
        int wordIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_WORD);
        int partOfSpeechIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_PARTOFSPEECH);
        int definitionIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_DEFINITION); //

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        int level = mCursor.getInt(levelIndex);
        final String word = mCursor.getString(wordIndex);
        final String partOfSpeech = mCursor.getString(partOfSpeechIndex);
        final String definition = mCursor.getString(definitionIndex);

        viewHolder.itemView.setTag(id);
        viewHolder.tvWord.setText(word);
        viewHolder.tvPartOfSpeech.setText(partOfSpeech);

        String levelString = "" + level;
        viewHolder.tvLevel.setText(levelString);
        // set level color
        GradientDrawable levelCircle = (GradientDrawable) viewHolder.tvLevel.getBackground();
        int levelColor = getLevelColor(level);
        levelCircle.setColor(levelColor);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getPos = String.valueOf(position);
//                Toast.makeText(mContext, getPos, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("ID", id);

                intent.putExtra("WORD", word);
                intent.putExtra("PARTOFSPEECH", partOfSpeech);
                intent.putExtra("DEFINITION", definition);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLevel, tvWord, tvPartOfSpeech;

        public ViewHolder(View itemView) {
            super(itemView);

            tvLevel = (TextView) itemView.findViewById(R.id.tvLevel);
            tvWord = (TextView) itemView.findViewById(R.id.tvWord);
            tvPartOfSpeech = (TextView) itemView.findViewById(R.id.tvPartOfSpeech);
        }

    }

    private int getLevelColor(int level) {
        int levelColor = 0;

        switch(level) {
            case 3: levelColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: levelColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 1: levelColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default: break;
        }
        return levelColor;
    }

    //CursorAdapter提供 swapCursor() 方法來更新Cursor，我們需要在RecyclerView的Adapter中提供自己的實現
    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


}


