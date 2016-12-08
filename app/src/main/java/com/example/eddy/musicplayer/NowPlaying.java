package com.example.eddy.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NowPlaying extends AppCompatActivity {

    /**
     * Instance variables
     */
    private TextView playingSongName;
    private TextView playingArtist;
    private ImageView shuffleView;
    private ImageView previousPressView;
    private ImageView pausePlayPressView;
    private ImageView nextPressView;
    private String songName;
    private boolean playbackPaused = false;
    private boolean firstTimeRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        //Reference the image views and set listeners to them
        //****Apparently shuffle does not want to show up****
        /*
        shuffleView = (ImageView) findViewById(R.id.shuffle);
        shuffleView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                shuffle();
            }
        });*/
        previousPressView = (ImageView) findViewById(R.id.previousAction);
        previousPressView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPrevious();
            }
        });
        pausePlayPressView = (ImageView) findViewById(R.id.pauseAction);
        pausePlayPressView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        nextPressView = (ImageView) findViewById(R.id.nextAction);
        nextPressView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        //Set the image view and retrieve the song name from the service
        playingSongName = (TextView) findViewById(R.id.nowPlayingSongName);
        playingArtist = (TextView) findViewById(R.id.nowPlayingArtist);
        //songName = musicService.getSongTitle();
        //playingSongName.setText(songName);
    }

    /**
     * This will mostly be used for when switching back and forth from activities
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Wanted to set this up if user clicked on the Relative Layout first before clicking
        //a song to play from the list.
        if(!firstTimeRunning) {
            //Reset the song name
            //songNameView.setText(musicSrv.getSongTitle());
            //Change the play/pause icon accordingly if needed
            //changePlayPauseIcon(musicService.songPlayingQuestion());
        }

        firstTimeRunning = false;
    }



    /**
     * This next part is for testing purposes
     */
    private Intent playIntent2;
    private MusicService musicService;
    private boolean musicBound2 = false;
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent2 == null) {
            playIntent2 = new Intent(this, MusicService.class);
            bindService(playIntent2, musicConnection2, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Connect to the service
     */
    private ServiceConnection musicConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //Get service
            musicService = binder.getService();
            playingSongName.setText(musicService.getSongTitle());
            playingArtist.setText(musicService.getArtistName());
            changePlayPauseIcon(musicService.songPlayingQuestion());
            musicBound2 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound2 = false;
        }
    };

    /**
     * End of testing purposes
     */


    private void shuffle() {
        musicService.setShuffle();
    }

    private void playPrevious() {
        musicService.playPrev();
        changePlayPauseIcon(musicService.songPlayingQuestion());
        playingSongName.setText(musicService.getSongTitle());
        playingArtist.setText(musicService.getArtistName());
    }

    private void playPause() {
        musicService.playPause();
        changePlayPauseIcon(musicService.songPlayingQuestion());
        playingSongName.setText(musicService.getSongTitle());
        playingArtist.setText(musicService.getArtistName());
    }

    private void playNext() {
        musicService.playNext();
        changePlayPauseIcon(musicService.songPlayingQuestion());
        playingSongName.setText(musicService.getSongTitle());
        playingArtist.setText(musicService.getArtistName());
    }

    private void changePlayPauseIcon(boolean songPlayingState) {
        if(!songPlayingState) {
            pausePlayPressView.setImageResource(R.drawable.play2);
        }
        else {
            pausePlayPressView.setImageResource(R.drawable.pause);
        }
    }
}