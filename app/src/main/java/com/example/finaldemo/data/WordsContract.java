package com.example.finaldemo.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WordsContract {

    public static final String AUTHORITY = "com.example.android.finaldemo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_WORDS = "words";

    public static final class WordsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static final String TABLE_NAME = "words"; //如果有新增的欄位要重改DB名或TABLE名

        public static final String _ID = "id";
        public static final String COL_LEVEL = "level";
        public static final String COL_WORD = "word";
        public static final String COL_PARTOFSPEECH = "partOfSpeech";
        public static final String COL_DEFINITION = "definition";

    }

}
