package com.gamexeed.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_pause, btn_next, btn_previous;
    TextView songLabel;
    SeekBar seekBar;
    int position;
    static MediaPlayer mediaPlayer;
    ArrayList<File> mySongs;
    Thread thread;
    String sName;
    TextView time_left, time_right;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        btn_pause = findViewById(R.id.btn_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        seekBar = findViewById(R.id.seek_bar);
        songLabel = findViewById(R.id.songLabel);
        time_left = findViewById(R.id.tv_left);
        time_right = findViewById(R.id.tv_right);

        btn_next.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_previous.setOnClickListener(this);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        thread = new Thread() {
            @Override
            public void run() {
                int total_duration = mediaPlayer.getDuration();
                int current_duration = 0;
                while (current_duration < total_duration) {
                    try {
                        sleep(100);
                        current_duration = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(current_duration);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        mySongs = (ArrayList) extras.getParcelableArrayList("songs");
        sName = mySongs.get(position).getName().toString();

        String songName = extras.get("songName").toString();

        songLabel.setText(songName);
        songLabel.setSelected(true);

        position = extras.getInt("position", 0);

        Uri uri_parse = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri_parse);

        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        thread.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == seekBar.getMax())
                    btn_pause.setBackgroundResource(R.drawable.play_btn);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pause:
                seekBar.setMax(mediaPlayer.getDuration());
                if (mediaPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.play_btn);
                    mediaPlayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.pause_btn);
                    mediaPlayer.start();
                }
                break;
            case R.id.btn_next:
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                prevAndNext();
                break;
            case R.id.btn_previous:
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) % mySongs.size());
                prevAndNext();
                break;
        }

    }

    private void prevAndNext() {
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        sName = mySongs.get(position).getName();
        songLabel.setText(sName);
        mediaPlayer.start();
        btn_pause.setBackgroundResource(R.drawable.pause_btn);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onBackPressed();
    }

}
