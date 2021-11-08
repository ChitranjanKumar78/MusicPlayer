package com.chitranjank.co.musicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Player extends AppCompatActivity {
    static MediaPlayer mediaPlayer = new MediaPlayer();
    TextView textViewCurrentPos, textViewMaxPos, textView;
    SeekBar seekBar;
    Runnable runnable;
    Handler handler = new Handler();

    private Button buttonPlay;
    private Button buttonNext;
    private Button buttonPrevious;

    Button repeat, shuffle;

    boolean repeatSong = true;
    boolean shuffleList = true;
    boolean res = true;
    int previousPositon = 0;
    ArrayList<Songs> arrayList;
    ArrayList<Integer> integerArrayList;

    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Play Songs");

        buttonPlay = findViewById(R.id.b_play);
        buttonNext = findViewById(R.id.b_next);
        buttonPrevious = findViewById(R.id.b_prev);
        listView = findViewById(R.id.lv);

        repeat = findViewById(R.id.b_repreat);
        shuffle = findViewById(R.id.b_shuffle);

        textView = findViewById(R.id.t);
        textViewCurrentPos = findViewById(R.id.textView);
        textViewMaxPos = findViewById(R.id.textView2);
        seekBar = findViewById(R.id.seekBar);

        Intent bundle = getIntent();
        integerArrayList = new ArrayList<>();

        String title = bundle.getStringExtra("Title");
        Uri uri = Uri.parse(bundle.getStringExtra("Uri"));


        if (!mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(this, uri);
            textView.setText(title);
            mediaPlayer.start();
            buttonPlay.setBackgroundResource(R.drawable.pouse_b);
            seekBar.setMax(mediaPlayer.getDuration());
            setSeekBarUpdate();
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(this, uri);
            textView.setText(title);
            mediaPlayer.start();
            buttonPlay.setBackgroundResource(R.drawable.pouse_b);
            seekBar.setMax(mediaPlayer.getDuration());
            setSeekBarUpdate();
        }

        arrayList = new ArrayList<>();
        getSongsFromStorage();

        previousPositon = bundle.getIntExtra("Pos", 0);

        int i = 0;
        while (i < arrayList.size() - 1) {
            integerArrayList.add(i);
            i++;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    previousPositon = position;
                    playSongsByMediaplayer(previousPositon);
                } else {
                    previousPositon = position;
                    playSongsByMediaplayer(previousPositon);
                }

            }
        });

        buttonAssigning();

    }

    private void buttonAssigning() {
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    res = false;
                    buttonPlay.setBackgroundResource(R.drawable.play_on);
                } else {
                    mediaPlayer.start();
                    setSeekBarUpdate();
                    res = true;
                    buttonPlay.setBackgroundResource(R.drawable.pouse_b);
                }
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                if (previousPositon <= 0) {
                    previousPositon = arrayList.size() - 1;
                } else {
                    previousPositon--;
                }
                playSongsByMediaplayer(previousPositon);
                res = true;
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                if (previousPositon < arrayList.size() - 1) {
                    previousPositon++;
                } else {
                    previousPositon = 0;
                }
                playSongsByMediaplayer(previousPositon);
                res = true;
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleList) {
                    shuffle.setBackgroundResource(R.drawable.sh_on);
                    shuffleList = !shuffleList;
                    repeat.setBackgroundResource(R.drawable.repeat_off);
                    repeatSong = true;
                } else {
                    shuffle.setBackgroundResource(R.drawable.sh_off);
                    shuffleList = !shuffleList;
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatSong) {
                    repeat.setBackgroundResource(R.drawable.repeat_on);
                    repeatSong = !repeatSong;
                    shuffle.setBackgroundResource(R.drawable.sh_off);
                    shuffleList = true;
                } else {
                    repeat.setBackgroundResource(R.drawable.repeat_off);
                    repeatSong = !repeatSong;
                }
            }
        });
    }

    public void setSeekBarUpdate() {
        String maxDuration = createTimer(mediaPlayer.getDuration());
        textViewMaxPos.setText(maxDuration);

        final int currentPos = mediaPlayer.getCurrentPosition();
        String string = createTimer(currentPos);
        textViewCurrentPos.setText(string);

        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress(currentPos);
            runnable = new Runnable() {
                @Override
                public void run() {
                    setSeekBarUpdate();
                }
            };
            handler.postDelayed(runnable, 1000);
        } else {
            if (res) {
                if (!repeatSong) {
                    playSongsByMediaplayer(previousPositon);
                } else if (!shuffleList) {
                    Collections.shuffle(integerArrayList);
                    playSongsByMediaplayer(integerArrayList.get(previousPositon));
                } else {
                    if (previousPositon < arrayList.size() - 1) {
                        previousPositon++;
                    } else {
                        previousPositon = 0;
                    }
                    playSongsByMediaplayer(previousPositon);
                }
            }
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                setSeekBarUpdate();
            }
        });
    }

    private void playSongsByMediaplayer(int previousPositon) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(this, arrayList.get(previousPositon).getUri());
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            textView.setText(arrayList.get(previousPositon).getTitle());
            buttonPlay.setBackgroundResource(R.drawable.pouse_b);
        } else {
            mediaPlayer = MediaPlayer.create(this, arrayList.get(previousPositon).getUri());
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            textView.setText(arrayList.get(previousPositon).getTitle());
            buttonPlay.setBackgroundResource(R.drawable.pouse_b);
        }
        setSeekBarUpdate();
    }

    public String createTimer(int duration) {
        String timerLable = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timerLable += min + ":";

        if (sec < 10) {
            timerLable += "0";
        }

        timerLable += sec;

        return timerLable;
    }

    private void getSongsFromStorage() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            title = title.replace("_", " ");
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            Uri song = Uri.withAppendedPath(uri, id);

            arrayList.add(new Songs(title, song));
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.show_songs) {
            listView.setVisibility(View.VISIBLE);
            MainActivity.SongsAdapter adapter = new MainActivity.SongsAdapter(arrayList);
            listView.setAdapter(adapter);
        }

        return super.onOptionsItemSelected(item);
    }
}