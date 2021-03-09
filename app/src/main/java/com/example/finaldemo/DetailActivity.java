package com.example.finaldemo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.finaldemo.data.WordsContract;

public class DetailActivity extends AppCompatActivity {
    private TextView tvDetailWord, tvDetailPartOfSpeech, tvDetailDefinition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        findView();

        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", 0);

        ContentResolver contentResolver = getContentResolver();
        Uri uri = WordsContract.WordsEntry.CONTENT_URI;
        String select = "id = " + String.valueOf(id);
        Cursor cursor = contentResolver.query(uri, null, select, null, "word asc");
        int wordIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_WORD);
        int partOfSpeechIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_PARTOFSPEECH);
        int definitionIndex = cursor.getColumnIndex(WordsContract.WordsEntry.COL_DEFINITION);

        cursor.moveToFirst();

        tvDetailWord.setText(cursor.getString(wordIndex));
        tvDetailPartOfSpeech.setText(cursor.getString(partOfSpeechIndex));
        tvDetailDefinition.setText(cursor.getString(definitionIndex));

//        tvDetailWord.setText(intent.getStringExtra("WORD"));
//        tvDetailPartOfSpeech.setText(intent.getStringExtra("PARTOFSPEECH"));
//        tvDetailDefinition.setText(intent.getStringExtra("DEFINITION"));

        fabOnUpdateClick();

    }

    private void fabOnUpdateClick() {
        FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(DetailActivity.this, UpdateActivity.class);
                updateIntent.putExtra("WORD", tvDetailWord.getText());
                startActivity(updateIntent);
                finish();
            }
        });
    }

    public void findView(){
        tvDetailWord = (TextView) findViewById(R.id.tvDetailWord);
        tvDetailPartOfSpeech = (TextView) findViewById(R.id.tvDetailPartOfSpeech);
        tvDetailDefinition = (TextView) findViewById(R.id.tvDetailDefinition);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
