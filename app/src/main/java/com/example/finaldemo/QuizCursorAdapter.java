package com.example.finaldemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finaldemo.data.WordsContract;

public class QuizCursorAdapter extends RecyclerView.Adapter<QuizCursorAdapter.ViewHolder> {

    public Cursor mCursor;
    private Context mContext;

    public QuizCursorAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.quizlist_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        // 訪問 Cursor 的指標獲得其中的數據:
        // int ColumnIndex = Cursor.getColumnIndex(欄位名稱);
        // String ColumnName = Cursor.getString(ColumnIndex);

        int idIndex = mCursor.getColumnIndex(WordsContract.WordsEntry._ID);
        int wordIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_WORD);
        int definitionIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COL_DEFINITION);

        mCursor.moveToPosition(pos); // 移動指標到一個絕對的位置

        final int id = mCursor.getInt(idIndex);
        final String word = mCursor.getString(wordIndex);
        final String definition = mCursor.getString(definitionIndex);

        viewHolder.itemView.setTag(id);
        viewHolder.tvQuizWord.setText(word);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerIntent = new Intent(mContext, AnswerActivity.class);
                answerIntent.putExtra("DEFINITION", definition);
                answerIntent.putExtra("WORD", word);
                answerIntent.putExtra("ITEMSCOUNT", getItemCount());
                answerIntent.putExtra("ID", id);
                mContext.startActivity(answerIntent);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvQuizWord ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizWord = (TextView) itemView.findViewById(R.id.tvQuizWord);
        }
    }

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
