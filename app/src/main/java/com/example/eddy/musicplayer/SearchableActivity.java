package com.example.eddy.musicplayer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    //Used for logging
    private static final String TAG = "SEARCHABLE_MESSAGE";

    /**
     * Variables
     */
    private ListView searchListView;
    private DatabaseHandler databaseObject;
    private ArrayList<Song> searchResults;
    //Variables for the RelativeLayout
    private ImageView previousActionView;
    private ImageView pauseActionView;
    private ImageView nextActionView;
    private RelativeLayout relativeLayoutView;
    private TextView songNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Entered onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //Reference the variables
        databaseObject = new DatabaseHandler(this);
        searchListView = (ListView) findViewById(R.id.searchListView);
        //TODO possibly set a listener to handle the search list view

        //Reference everything in the RelativeLayout and set listeners to them
        previousActionView = (ImageView) findViewById(R.id.previousActionSearch);
        previousActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPrev();
            }
        });
        pauseActionView = (ImageView) findViewById(R.id.pauseActionSearch);
        pauseActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        nextActionView = (ImageView) findViewById(R.id.nextActionSearch);
        nextActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        relativeLayoutView = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);
        songNameView = (TextView) findViewById(R.id.songNameTextSearch);

        handleIntent(getIntent());
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        //Search the database for the query and return the results to an ArrayList
        searchResults = databaseObject.searchDatabase(query);

        //Set an adapter to the ArrayList for the ListView
        SongAdapter searchResultsAdapter = new SongAdapter(this, searchResults);
        searchListView.setAdapter(searchResultsAdapter);
    }

    private void changePlayPauseIcon(boolean songPlayingState) {
        if(!songPlayingState) {
            pauseActionView.setImageResource(R.drawable.play2);
        }
        else {
            pauseActionView.setImageResource(R.drawable.pause);
        }
    }

    public void songPicked(View view) {
        musicSrv.setSongFromSearch(Integer.parseInt(view.getTag().toString()), searchResults);
        musicSrv.playSongFromSearch();
        relativeLayoutView.setVisibility(View.VISIBLE);
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playPrev(){
        musicSrv.playPrevForSearch();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playPause(){
        musicSrv.playPause();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    private void playNext(){
        musicSrv.playNextForSearch();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }
}
