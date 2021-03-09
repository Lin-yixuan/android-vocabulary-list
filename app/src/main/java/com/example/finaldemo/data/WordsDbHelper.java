package com.example.finaldemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finaldemo.data.WordsContract.WordsEntry;

public class WordsDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wordDB.db";
    private static final int DB_VERSION = 1;

    public WordsDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + WordsEntry.TABLE_NAME + "( " +
                WordsEntry._ID + " INTEGER PRIMARY KEY, " +
                WordsEntry.COL_LEVEL + " INTEGER NOT NULL, " +
                WordsEntry.COL_WORD + " TEXT NOT NULL, " +
                WordsEntry.COL_PARTOFSPEECH + " TEXT NOT NULL, " +
                WordsEntry.COL_DEFINITION + " TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordsEntry.TABLE_NAME);
        onCreate(db);
    }
}
