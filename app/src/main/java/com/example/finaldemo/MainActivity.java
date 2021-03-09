package com.example.finaldemo;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.finaldemo.data.WordsContract;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    MediaPlayer mp;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;

    private WordsCursorAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvWords);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WordsCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = WordsContract.WordsEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        fabOnQuizClick();

        sampleDataAdd(); // 如果資料庫為空時，會新增預設的5筆資料
        setupPermissions(); // 查看是否取得權限


        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

    }

    // floating action bar
    public void fabOnQuizClick() {
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fabQuiz);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QuizIntent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(QuizIntent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_barChangeSort:
                ContentResolver contentResolver = getContentResolver();
                Uri uri = WordsContract.WordsEntry.CONTENT_URI;
                Cursor cursor = contentResolver.query(uri, null, null, null, "word asc");
                mAdapter.swapCursor(cursor);
        }
        return true;
    }

    // Click Add new word action bar
    public void onClickAddActionBar(MenuItem item) {
        Intent addWordIntent = new Intent(MainActivity.this, AddWordActivity.class);
        startActivity(addWordIntent);
    }
    // Click Setting music action bar
    public void onClickSettingActionBar(MenuItem item) {
        Intent BackMusicIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(BackMusicIntent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(WordsContract.WordsEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            WordsContract.WordsEntry._ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // 當Activity畫面要呈現時會呼叫此方法
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    // 確認使用者取得權限後，會呼叫此方法
    public void setupSharedPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String newMusicKey = sharedPreferences.getString(getString(R.string.pref_music_key), getString(R.string.pref_music_label_music1));
        if (newMusicKey.equals(getString(R.string.pref_music_music2_value))) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.music2);
            mp.start();
        } else if (newMusicKey.equals(getString(R.string.pref_music_label_music3))) {
            mp = null;
        } else {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.music1);
            mp.start();
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_music_key))) {
            String newMusicKey = sharedPreferences.getString(getString(R.string.pref_music_key), getString(R.string.pref_music_music1_value));
            if (mp != null) {
                if (mp.isPlaying())
                    mp.stop();
                if (newMusicKey.equals(getString(R.string.pref_music_music1_value))) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.music1);
                    mp.start();
                } else if (newMusicKey.equals(getString(R.string.pref_music_music2_value))) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.music2);
                    mp.start();
                } else if (newMusicKey.equals(getString(R.string.pref_music_music3_value))) {
                    Toast.makeText(this, "No BGM.", Toast.LENGTH_LONG).show();
                    mp.stop();
                }
            } else {
                if (newMusicKey.equals(getString(R.string.pref_music_music1_value))) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.music1);
                    mp.start();
                } else if (newMusicKey.equals(getString(R.string.pref_music_music2_value))) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.music2);
                    mp.start();
                } else if (newMusicKey.equals(getString(R.string.pref_music_music3_value))) {
                    Toast.makeText(this, "No BGM.", Toast.LENGTH_LONG).show();
                    mp.stop();
                }
            }
        }
    }

    //Activity要結束之間會呼叫此方法建議此階段釋放所有未釋放的資源
    //取消註冊VisualizerActivity作為OnPreferenceChangedListener以避免任何內存洩漏。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null)
            mp.release();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void insertData (String word, String partOfSpeech, int level, String definition){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordsContract.WordsEntry.COL_WORD, word);
        contentValues.put(WordsContract.WordsEntry.COL_PARTOFSPEECH, partOfSpeech);
        contentValues.put(WordsContract.WordsEntry.COL_LEVEL, level);
        contentValues.put(WordsContract.WordsEntry.COL_DEFINITION, definition);
        getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, contentValues);
    }

    public void sampleDataAdd(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(WordsContract.WordsEntry.CONTENT_URI, null, null, null, null);
        int result = 0;
        if(cursor.moveToNext())
            result = cursor.getInt(0);

        if (result == 0) {
            insertData("concrete", "n.", 2, "● A very hard building material made by mixing together cement, sand, small stones, and water.");
            insertData("assist", "v.", 1, "● To take action to help someone or support something. \n● To help something develop or happen by providing money, support, etc.");
            insertData("vicious", "adj.", 2, "● Vicious people or actions show an intention or wish to hurt someone or something very badly. \n● Used to describe an object, condition, or remark that causes great physical or emotional pain.");
            insertData("interrupt", "v.", 3, "● To stop a person from speaking for a short period by something you say or do. \n● To stop someone from speaking by saying or doing something, or to cause an activity or event to stop briefly.");
            insertData("ambition", "n", 1, "● A strong wish to achieve something. \n● A strong desire for success, achievement, power, or wealth.");
        }
    }

    // 聲音權限設定
    //取得權限
    private void setupPermissions(){
        // 檢查是否已取得權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  未取得權限，再次詢問
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            //  已取得權限
            setupSharedPreference();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            // 詢問使用者是否給予權限
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  使用者同意
//                    Toast.makeText(this, "setUpPermissions", Toast.LENGTH_LONG).show();
                    setupPermissions();
                } else {
            // 使用者不同意
                    Toast.makeText(this, "Permission for audio not granted. MainActivity can't run.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    //  螢幕旋轉時不重新執行onCreate()
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
        else {
        }
    }


}
