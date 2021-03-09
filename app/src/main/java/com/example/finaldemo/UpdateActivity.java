package com.example.finaldemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.finaldemo.data.WordsContract;

public class UpdateActivity extends AppCompatActivity {
    private EditText etWord, etPartOfSpeech, etDefinition;
    private RadioButton rabEasy, rabMid, rabDifficult;
    private Button btUpdate;
    private int mLevel;
    public Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        findView();

        Intent intent = getIntent();
        String word = intent.getStringExtra("WORD");

        ContentResolver contentResolver = getContentResolver();
        Uri uri = WordsContract.WordsEntry.CONTENT_URI;
        String select = WordsContract.WordsEntry.COL_WORD + " = '" + word + "'";
        cursor = contentResolver.query(uri, null, select, null, null);

        int partOfSpeechIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_PARTOFSPEECH);
        int definitionIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_DEFINITION);
        int levelIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_LEVEL);


        cursor.moveToFirst();

        etWord.setText(word);
        etPartOfSpeech.setText(cursor.getString(partOfSpeechIndex));
        etDefinition.setText(cursor.getString(definitionIndex));

        int level = Integer.parseInt(cursor.getString(levelIndex));

        if (level == 1){
            mLevel = 1;
            rabEasy.setChecked(true);
        } else if (level == 2) {
            mLevel = 2;
            rabMid.setChecked(true);
        } else if (level == 3) {
            mLevel = 3;
            rabDifficult.setChecked(true);
        }

    }

    private void findView() {
        etWord = (EditText) findViewById(R.id.etAddWord);
        etPartOfSpeech = (EditText) findViewById(R.id.etAddPartOfSpeech);
        etDefinition = (EditText) findViewById(R.id.etAddDefinition);
        rabEasy = (RadioButton) findViewById(R.id.radButton1);
        rabMid = (RadioButton) findViewById(R.id.radButton2);
        rabDifficult = (RadioButton) findViewById(R.id.radButton3);
        btUpdate = (Button) findViewById(R.id.btAdd);
        btUpdate.setText("Update");
    }

    public void onAddWordClick(View view) {
        int idIndex = cursor.getColumnIndex(WordsContract.WordsEntry._ID);
        int id = cursor.getInt(idIndex);
        String select = WordsContract.WordsEntry._ID + " = " + String.valueOf(id);
        Uri uri = WordsContract.WordsEntry.CONTENT_URI;

        ContentValues contentValues = new ContentValues();
        contentValues.put(WordsContract.WordsEntry.COL_LEVEL, mLevel);
        contentValues.put(WordsContract.WordsEntry.COL_WORD, String.valueOf(etWord.getText()));
        contentValues.put(WordsContract.WordsEntry.COL_PARTOFSPEECH, String.valueOf(etPartOfSpeech.getText()));
        contentValues.put(WordsContract.WordsEntry.COL_DEFINITION, String.valueOf(etDefinition.getText()));
        getContentResolver().update(uri, contentValues, select, null);
        finish();

    }

    public void onLevelSelected(View view) {
        if (rabEasy.isChecked())
            mLevel = 1;
        else if (rabMid.isChecked())
            mLevel = 2;
        else if (rabDifficult.isChecked())
            mLevel = 3;
    }
}
