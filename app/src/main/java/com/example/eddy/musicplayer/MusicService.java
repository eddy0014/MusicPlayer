package com.example.eddy.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

/**
 * Created by Eddy on 6/29/2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    /**
     * Variables
     */
    //Media player
    private MediaPlayer player;
    //Song list
    private ArrayList<Song> songs;
    //Current position for song from regular list
    private int songPosn;
    //Current position for song from search results list
    //ArrayList holding the songs from the search results list
    private int songIndex = 0;
    private ArrayList<Song> songsFromSearch;
    //Current position for song from artist results list
    //ArrayList holding the songs from the artists results list
    private int songIndex2 = 0;
    private ArrayList<Song> songsFromArtist;
    //Current position for song from album results list
    //ArrayList holding the songs from the albums results list
    private int songIndex3 = 0;
    private ArrayList<Song> songsFromAlbum;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle = " ";
    private String artistName = " ";
    private static final int NOTIFY_ID = 1;
    private boolean shuffle = false;
    private Random rand;
    private boolean songPlaying = false;

    /**
     * Need these implemented
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public void onCreate() {
        //create the service
        super.onCreate();
        //Initialize position
        songPosn = 0;
        //Create player
        player = new MediaPlayer();

        initMusicPlayer();

        //Initiate random number for shuffle
        rand = new Random();
    }

    public void initMusicPlayer() {
        //Set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    /**
     * Set the current song
     * Call this when the user picks a song from the list
     */
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public void setSongFromSearch(int index, ArrayList<Song> searchResultsArray) {
        //Set the global ArrayList equal to the local ArrayList
        songsFromSearch = searchResultsArray;
        songIndex = index;
    }

    public void setSongFromArtist(int index, ArrayList<Song> artistResultsArray) {
        //Set the global ArrayList equal to the local ArrayList
        songsFromArtist = artistResultsArray;
        songIndex2 = index;
    }

    public void setSongFromAlbum(int index, ArrayList<Song> albumResultsArray) {
        //Set the global ArrayList equal to the local ArrayList
        songsFromAlbum = albumResultsArray;
        songIndex3 = index;
    }

    public void playSong() {
        if(!songPlaying) {
            songPlaying = true;
        }
        //Reset the MediaPlayer
        player.reset();
        //Get the song
        Song playSong = songs.get(songPosn);
        songTitle = playSong.getTitle();
        artistName = playSong.getArtist();
        //Get id
        long currSong = playSong.getID();
        //Set uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        //Set URI as data source and catch error if needed
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source");
        }

        //Call asynchronous method of MediaPlayer to prepare playSong()
        player.prepareAsync();
    }

    /**
     * Play the song selected from the search results
     */
    public void playSongFromSearch() {
        if(!songPlaying) {
            songPlaying = true;
        }

        //Reset the MediaPlayer
        player.reset();
        //Get the song
        Song playSong = songsFromSearch.get(songIndex);
        songTitle = playSong.getTitle();
        artistName = playSong.getArtist();
        //Get id
        long currSong = playSong.getID();
        //Set uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        //Set URI as data source and catch error if needed
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source");
        }

        //Call asynchronous method of MediaPlayer to prepare playSong()
        player.prepareAsync();
    }

    /**
     * Play the song selected from the Artist songs results list
     */
    public void playSongFromArtist() {
        if(!songPlaying) {
            songPlaying = true;
        }

        //Reset the MediaPlayer
        player.reset();
        //Get the song
        Song playSong = songsFromArtist.get(songIndex2);
        songTitle = playSong.getTitle();
        artistName = playSong.getArtist();
        //Get id
        long currSong = playSong.getID();
        //Set uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        //Set URI as data source and catch error if needed
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source");
        }

        //Call asynchronous method of MediaPlayer to prepare playSong()
        player.prepareAsync();
    }

    /**
     * Play the song selected from the Album songs results list
     */
    public void playSongFromAlbum() {
        if(!songPlaying) {
            songPlaying = true;
        }

        //Reset the MediaPlayer
        player.reset();
        //Get the song
        Song playSong = songsFromAlbum.get(songIndex3);
        songTitle = playSong.getTitle();
        artistName = playSong.getArtist();
        //Get id
        long currSong = playSong.getID();
        //Set uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        //Set URI as data source and catch error if needed
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source");
        }

        //Call asynchronous method of MediaPlayer to prepare playSong()
        player.prepareAsync();
    }

    /**
     * When MediaPlayer is prepared, after going through prepareAsync(), this will be executed
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //Start playback
        mp.start();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    /**
     * Get the song title of the current song that is playing to display
     */
    public String getSongTitle() {
        if(songPlaying) {
            return songTitle;
        }
        else {
            return "";
        }
    }

    public String getArtistName() {
        if(songPlaying) {
            return artistName;
        }
        else {
            return "";
        }
    }

    /**
     * These next methods will be used to act on the control that is made in the
     * activity class
     */
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean songIsPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void play() {player.start();}

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public boolean songPlayingQuestion() {
        return songPlaying;
    }

    public void playPause() {
        if(!songPlaying) {
            songPlaying = true;
            player.start();
        }
        else if(songPlaying) {
            songPlaying = false;
            player.pause();
        }
    }

    /**
     * Methods for skipping to the next and previous tracks for the regular song list
     */
    public void playPrev() {
        if(!songPlaying) {
            songPlaying = true;
        }

        songPosn--;
        if(songPosn < 0) songPosn = songs.size()-1;
        playSong();
    }

    public void playNext() {
        if(!songPlaying) {
            songPlaying = true;
        }

        if(shuffle) {
            int newSong = songPosn;
            while(newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        }
        else{
            songPosn++;
            if(songPosn >= songs.size()) songPosn = 0;
        }
        playSong();
    }

    /**
     * Play previous and play next for artist results list
     */
    public void playPrevForArtist() {
        if(!songPlaying) {
            songPlaying = true;
        }

        songIndex2--;
        if(songIndex2 < 0) songIndex2 = songsFromArtist.size()-1;
        playSongFromArtist();
    }

    public void playNextForArtist() {
        if(!songPlaying) {
            songPlaying = true;
        }

        if(shuffle) {
            int newSong = songIndex2;
            while(newSong == songIndex2) {
                newSong = rand.nextInt(songsFromArtist.size());
            }
            songIndex2 = newSong;
        }
        else{
            songIndex2++;
            if(songIndex2 >= songsFromArtist.size()) songIndex2 = 0;
        }
        playSongFromArtist();
    }

    /**
     * Play previous and play next for album results list
     */
    public void playPrevForAlbum() {
        if(!songPlaying) {
            songPlaying = true;
        }

        songIndex3--;
        if(songIndex3 < 0) songIndex3 = songsFromAlbum.size()-1;
        playSongFromAlbum();
    }

    public void playNextForAlbum() {
        if(!songPlaying) {
            songPlaying = true;
        }

        if(shuffle) {
            int newSong = songIndex3;
            while(newSong == songIndex3) {
                newSong = rand.nextInt(songsFromAlbum.size());
            }
            songIndex3 = newSong;
        }
        else{
            songIndex3++;
            if(songIndex3 >= songsFromAlbum.size()) songIndex3 = 0;
        }
        playSongFromAlbum();
    }

    /**
     * Play previous and play next for search results list
     */
    public void playPrevForSearch() {
        if(!songPlaying) {
            songPlaying = true;
        }

        songIndex--;
        if(songIndex < 0) songIndex = songsFromSearch.size()-1;
        playSongFromSearch();
    }

    public void playNextForSearch() {
        if(!songPlaying) {
            songPlaying = true;
        }

        if(shuffle) {
            int newSong = songIndex;
            while(newSong == songIndex) {
                newSong = rand.nextInt(songsFromSearch.size());
            }
            songIndex = newSong;
        }
        else{
            songIndex++;
            if(songIndex >= songsFromSearch.size()) songIndex = 0;
        }
        playSongFromSearch();
    }

    /**
     * Stop setForeground when the service is destroyed
     */
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    /**
     * Method setting the shuffle flag
     */
    public void setShuffle() {
        if(shuffle) shuffle = false;
        else shuffle = true;
    }
}