package com.example.eddy.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowOther2 extends AppCompatActivity {

    /**
     * Variables
     */
    private ArrayList<Song> songsFromAlbum;
    private ListView resultsListView;
    //Variables for the RelativeLayout
    private ImageView previousActionView;
    private ImageView pauseActionView;
    private ImageView nextActionView;
    private RelativeLayout relativeLayoutView;
    private TextView songNameView;

    //Database variable
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_other2);

        //Reference the ListView
        resultsListView = (ListView) findViewById(R.id.resultsListAlbum);

        //Reference everything in the RelativeLayout and set listeners to them
        previousActionView = (ImageView) findViewById(R.id.previousActionAlbum);
        previousActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPrev();
            }
        });
        pauseActionView = (ImageView) findViewById(R.id.pauseActionAlbum);
        pauseActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        nextActionView = (ImageView) findViewById(R.id.nextActionAlbum);
        nextActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        relativeLayoutView = (RelativeLayout) findViewById(R.id.relativeLayoutAlbum);
        songNameView = (TextView) findViewById(R.id.songNameTextAlbum);

        //Get the string from the intent
        Intent intent = getIntent();
        String albumPickedName = intent.getExtras().getString("key3");

        //Search the database for songs that are from the album and get an ArrayList from it
        songsFromAlbum = db.searchAlbumPickedSongs(albumPickedName);

        //Use the adapter to set up the list view
        SongAdapter songAdapter = new SongAdapter(this, songsFromAlbum);
        resultsListView.setAdapter(songAdapter);
    }

    /**
     * This next part is for testing purposes
     */
    private Intent playIntent2;
    private MusicService musicSrv;
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
            musicSrv = binder.getService();
            //musicSrv.setList(searchResults);
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

    private void changePlayPauseIcon(boolean songPlayingState) {
        if(!songPlayingState) {
            pauseActionView.setImageResource(R.drawable.play2);
        }
        else {
            pauseActionView.setImageResource(R.drawable.pause);
        }
    }

    public void songPicked(View view) {
        musicSrv.setSongFromAlbum(Integer.parseInt(view.getTag().toString()), songsFromAlbum);
        musicSrv.playSongFromAlbum();
        relativeLayoutView.setVisibility(View.VISIBLE);
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playPrev(){
        musicSrv.playPrevForAlbum();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playPause(){
        musicSrv.playPause();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playNext(){
        musicSrv.playNextForAlbum();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }
}
