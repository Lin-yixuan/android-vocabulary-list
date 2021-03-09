package com.example.finaldemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.finaldemo.data.WordsContract;

public class AddWordActivity extends AppCompatActivity {

    private int mLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        mLevel = 1;
    }

    public void onAddWordClick(View view) {

        String inputWord = ((EditText) findViewById(R.id.etAddWord)).getText().toString();
        String inputPartOfSpeech = ((EditText) findViewById(R.id.etAddPartOfSpeech)).getText().toString();
        String inputDefinition = ((EditText) findViewById(R.id.etAddDefinition)).getText().toString();

        if (inputWord.length() == 0 || inputPartOfSpeech.length() == 0 ) {
            return;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(WordsContract.WordsEntry.COL_LEVEL, mLevel);
        contentValues.put(WordsContract.WordsEntry.COL_WORD, inputWord);
        contentValues.put(WordsContract.WordsEntry.COL_PARTOFSPEECH, inputPartOfSpeech);
        contentValues.put(WordsContract.WordsEntry.COL_DEFINITION, inputDefinition);

        Uri uri = getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, contentValues);

        if(uri != null) {
//            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        finish();
    }

    public void onLevelSelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mLevel = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mLevel = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mLevel = 3;
        }
    }

}
