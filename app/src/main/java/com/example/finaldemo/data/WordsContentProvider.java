package com.example.finaldemo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;

import static com.example.finaldemo.data.WordsContract.WordsEntry.TABLE_NAME;

public class WordsContentProvider extends ContentProvider {

    public static final int WORDS = 100;
    public static final int WORD_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(WordsContract.AUTHORITY, WordsContract.PATH_WORDS, WORDS);
        uriMatcher.addURI(WordsContract.AUTHORITY, WordsContract.PATH_WORDS + "/#", WORD_WITH_ID);
        return uriMatcher;
    }

    private WordsDbHelper mWordsDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mWordsDbHelper = new WordsDbHelper(context);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mWordsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case WORDS:
                long id = db.insert(TABLE_NAME, null, values);

                if(id > 0){
                    returnUri = ContentUris.withAppendedId(WordsContract.WordsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mWordsDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){
            case WORDS:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mWordsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int itemsDeleted;

        switch (match) {
            case WORD_WITH_ID:
                String id = uri.getPathSegments().get(1);
                itemsDeleted = db.delete(TABLE_NAME, "id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (itemsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated;
        int match = sUriMatcher.match(uri);

        switch (match){
            case WORDS:
//                String id = uri.getPathSegments().get(1);
                updated = mWordsDbHelper.getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if( updated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }
}
