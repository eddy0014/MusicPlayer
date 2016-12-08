package com.example.eddy.musicplayer;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.provider.MediaStore.Audio.Media;
import android.widget.Toast;

import com.example.eddy.musicplayer.MusicService.MusicBinder;

public class MainActivity extends AppCompatActivity {

    /**
     * Instance variables
     */
    private ArrayList<Song> songList;
    private ArrayList<String> artistList;
    private ArrayList<String> albumsList;
    private ListView songView;
    //Variables for the RelativeLayout
    private ImageView previousActionView;
    private ImageView pauseActionView;
    private ImageView nextActionView;
    private RelativeLayout relativeLayoutView;
    private TextView songNameView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private boolean paused = false;
    private boolean playbackPaused = false;
    private boolean firstTimeRunningValue = true;

    //Used for Shared Preferences
    SharedPreferences prefs;

    //Database variable
    DatabaseHandler db = new DatabaseHandler(this);

    //Used for testing purposes with logging
    private static final String TAG = "MY_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        //Set shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Reference the ImageViews
        //Set up listeners for them
        previousActionView = (ImageView) findViewById(R.id.previousAction);
        previousActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPrev();
            }
        });
        pauseActionView = (ImageView) findViewById(R.id.pauseAction);
        pauseActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        nextActionView = (ImageView) findViewById(R.id.nextAction);
        nextActionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        //Reference the RelativeLayout that holds the action buttons/imageviews
        //Set up a listener for it
        relativeLayoutView = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayoutView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, NowPlaying.class);
                String value = musicSrv.getSongTitle();
                myIntent.putExtra("key", value);
                MainActivity.this.startActivity(myIntent);
            }
        });
        //Reference the TextView for the song name
        songNameView = (TextView) findViewById(R.id.songNameText);

        //Check to see if this is the first time running the app
        //If so, scan for songs
        /*if(!prefs.getBoolean("firstTimeRunning", false)) {
            //Scan for songs to add to the database list
            scanForSongs();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTimeRunning", true);
            editor.commit();*/
        if(prefs.getBoolean("firstTimeRunning", true)) {
            //Scan for songs to add to the database list
            scanForSongs();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTimeRunning", false);
            editor.commit();
        }

        //Get a list of the songs from the already-made database
        //getSongList();

        //Instance of the adapter class
        /*SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        return true;
    }

    /**
     * Control what happens when a menu item is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu item selected
        switch(item.getItemId()) {
            case R.id.scanOption:
                scanForSongs();
                break;
            /*case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "Called onRestart().");
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if(playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.v(TAG, "startService() called.");
        }*/
        Log.v(TAG, "Called onStart().");
    }

    /**
     * This will mostly be used for when switching back and forth from activities
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Called onResume()");
        getSongList();
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);

        if(!firstTimeRunningValue) {
            //Reset the song name
            songNameView.setText(musicSrv.getSongTitle());
            //Change the play/pause icon accordingly if needed
            changePlayPauseIcon(musicSrv.songPlayingQuestion());
        }

        firstTimeRunningValue = false;

        //Testing
        if(playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.v(TAG, "startService() called.");
        }
    }

    /**
     * Connect to the service and set the song list for the music service to use
     */
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //Get service
            musicSrv = binder.getService();
            Log.v(TAG, "binder.getservice called");
            //Pass llst
            musicSrv.setList(songList);
            Log.v(TAG, "New list has been set.");
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /**
     * Scan for songs on the device and add them to the database.
     * This is only called the first time the app runs or when user decides to scan device.
     * This is using a strict path so this NEEDS TO BE CHANGED
     * It seems the strict path concern might be fixed
     */
    public void scanForSongs() {
        Log.v(TAG, "Started looking for songs");
        Toast.makeText(getApplicationContext(), "Started scanning", Toast.LENGTH_SHORT).show();
        /*ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //Iterate over results
        //Check if we have valid data
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int displayNameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            //Add the song to the list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String path = musicCursor.getString(pathColumn);
                String displayName = musicCursor.getString(displayNameColumn);
                Log.v(TAG, thisTitle + " name is " + displayName);
                String correctPath = "/storage/emulated/0/Music/" + displayName;
                if(path.equals(correctPath)) {
                    songList.add(new Song(thisId, thisTitle, thisArtist));
                    Log.v(TAG, thisTitle + " was added ");
                }
            }
            while (musicCursor.moveToNext());
        }*/


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;
        try {
            Uri uri = Media.EXTERNAL_CONTENT_URI;
            cursor = this.getContentResolver().query(uri, projection, selection, null, sortOrder);
            //either use this, getApplicationContext, or MainActivity.this for previous line
            if( cursor != null){
                cursor.moveToFirst();
                while( !cursor.isAfterLast() ){
                    //Made this Media since MediaData did not make sense and imported the library for it
                    Media media = new Media();
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String album = cursor.getString(2);
                    String path = cursor.getString(3);
                    String displayName  = cursor.getString(4);
                    String songDuration = cursor.getString(5);
                    String songID = cursor.getString(6);
                    //Add the song to the database
                    db.addSong(new Song(Integer.parseInt(songID), title, artist, album));
                    //songList.add(new Song(Long.parseLong(songID), title, artist));
                    Log.v(TAG, title + " scanned and added to database.");
                    Log.v(TAG, album + " from " + title);
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }

        Toast.makeText(getApplicationContext(), "Device scanned", Toast.LENGTH_SHORT).show();
    }

    /**
     * Retrieve a list of the songs from the database so it can be used to set up the listview
     */
    public void getSongList() {
        songList = db.getAllSongs();
    }

    /**
     * Check whether the music is paused or not and change the pause or play icon
     * accordingly
     */
    private void changePlayPauseIcon(boolean songPlayingState) {
        if(!songPlayingState) {
            pauseActionView.setImageResource(R.drawable.play2);
        }
        else {
            pauseActionView.setImageResource(R.drawable.pause);
        }
    }

    /**
     * Called when a song is selected from the list directly
     */
    public void songPicked(View view) {
        /*if(playbackPaused) {
            playbackPaused = false;
        }*/
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    /**
     * Called when the previous icon is pressed
     */
    private void playPrev() {
        /*if(playbackPaused) {
            playbackPaused = false;
        }*/
        musicSrv.playPrev();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    /**
     * Called when the play or pause icon is pressed
     */
    public void playPause() {
        /*if(playbackPaused) {
            playbackPaused = false;
            changePlayPauseIcon(playbackPaused);
            musicSrv.play();
            songNameView.setText(musicSrv.getSongTitle());
        }
        else if (!playbackPaused) {
            playbackPaused = true;
            changePlayPauseIcon(playbackPaused);
            musicSrv.pausePlayer();
            songNameView.setText(musicSrv.getSongTitle());
        }*/

        musicSrv.playPause();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    /**
     * Called when the next icon is pressed
     */
    public void playNext() {
        /*if(playbackPaused) {
            playbackPaused = false;
        }*/
        musicSrv.playNext();
        changePlayPauseIcon(musicSrv.songPlayingQuestion());
        songNameView.setText(musicSrv.getSongTitle());
    }

    /**
     * When the Tracks tab is clicked, this will show the list of tracks
     */
    public void showTracksList(View v) {
        //Get the song list from the already-made database
        songList = db.getAllSongs();

        //Use the adapter to set up the list view
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
    }

    /**
     * When the Artists tab is clicked, this will show the list of artists
     */
    public void showArtistsList(View v) {
        //Get the artist list from the table with songs
        artistList = db.getAllArtists();

        //Use the adapter to set up the list view with the new list
        ArtistAdapter artistAdapter = new ArtistAdapter(this, artistList);
        songView.setAdapter(artistAdapter);
    }

    public void artistPicked(View view) {
        //Get the name of the artist that was picked
        String artistPickedName = view.getTag().toString();

        //Start the new activity with the songs associated with the artist picked
        Intent artistIntent = new Intent(MainActivity.this, ShowOther.class);
        String value = artistPickedName;
        artistIntent.putExtra("key2", value);
        MainActivity.this.startActivity(artistIntent);
    }

    public void showAlbumsList(View v) {
        //Get the albums list from the table with songs
        albumsList = db.getAllAlbums();

        //Use the adapter to set up the list view with the new list
        AlbumAdapter albumAdapter = new AlbumAdapter(this, albumsList);
        songView.setAdapter(albumAdapter);
    }

    public void albumPicked(View view) {
        //Get the name of the album that was picked
        String albumPickedName = view.getTag().toString();

        //Start the new activity with the songs associated with the album picked
        Intent albumIntent = new Intent(MainActivity.this, ShowOther2.class);
        String value = albumPickedName;
        albumIntent.putExtra("key3", value);
        MainActivity.this.startActivity(albumIntent);
    }

    /**
     * This method should keep the service from destroying itself when
     * switching activities.
     */
    @Override
    protected void onStop() {
        Log.v(TAG, "Called onStop().");
        super.onStop();
    }

    /**
     * Called when the app is destroyed.
     * This destroys the service as well.
     */
    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }
}