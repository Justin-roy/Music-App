package com.example.darkmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity2 extends AppCompatActivity {
    Button Play,Goback,previous,next;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    TextView increase,decrease,SongText;
    int TotalDuration;
    int Pos;
    ArrayList<File> Playsongs;
    String SongName;
    @Override
    public void onBackPressed() {
       super.onBackPressed();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        SongText = findViewById(R.id.SongText);
        Goback = findViewById(R.id.Goback);
        increase = findViewById(R.id.increase);
        decrease = findViewById(R.id.decrease);
        seekBar = findViewById(R.id.seekBar);
        Play = findViewById(R.id.Play);
        Play.setBackgroundResource(R.drawable.pause_circle);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent I = getIntent();
        Bundle bundle = I.getExtras();

        Playsongs = (ArrayList) bundle.getParcelableArrayList("songsName");
        SongName = I.getStringExtra("songTitle");
        Pos = bundle.getInt("position", 0);
        SongText.setSelected(true);
        SongText.setText(SongName);
        Uri uri = Uri.parse(Playsongs.get(Pos).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        TotalDuration = mediaPlayer.getDuration();
        seekBar.setMax(TotalDuration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Goback.setOnClickListener(view -> {
            Intent Nintent = new Intent(MainActivity2.this,SongList.class);
            startActivity(Nintent);
            mediaPlayer.stop();
            mediaPlayer.release();
        });
        Play.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                Play.setBackgroundResource(R.drawable.play_circle);
                mediaPlayer.pause();
            } else {
                Play.setBackgroundResource(R.drawable.pause_circle);
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);;
            }
        });
        previous.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(Pos!=0)
            {
                Pos = Pos - 1;
            }
            else{
                Pos = Playsongs.size() - 1;
            }
            Uri uri1 = Uri.parse(Playsongs.get(Pos).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
            mediaPlayer.start();
            TotalDuration = mediaPlayer.getDuration();
            seekBar.setMax(TotalDuration);
            String s = Playsongs.get(Pos).getName().toString();
            SongText.setText(s);
        });
        next.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(Pos!=Playsongs.size() - 1)
            {
                Pos = Pos + 1;
            }
            else{
                Pos = 0;
            }
            Uri uri2 = Uri.parse(Playsongs.get(Pos).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri2);
            mediaPlayer.start();
            TotalDuration = mediaPlayer.getDuration();
            seekBar.setMax(TotalDuration);
            String s = Playsongs.get(Pos).getName().toString();
            SongText.setText(s);
        });
        //Up date song time line
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(message);
                    } catch (Exception e ) {
                          e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message message) {
            int SeekBarPosition = message.what;
            //Update song seek bar
            seekBar.setProgress(SeekBarPosition);

            //Update Labels
            String Time = createTimeText(SeekBarPosition);
            increase.setText(Time);

            //Time calculation
            String remainingTime = createTimeText(TotalDuration - SeekBarPosition);
            decrease.setText("- " + remainingTime);
        }
    };
    //Time Shows
    public String createTimeText(int time){
        String timeText;
        int min =  time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeText = min + ":";
        if (sec < 10 ) timeText += "0";
        timeText += sec;
        return timeText;
    }

}
