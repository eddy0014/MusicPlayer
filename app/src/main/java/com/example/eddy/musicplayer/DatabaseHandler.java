package com.example.eddy.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DatabaseHandler extends SQLiteOpenHelper{

    //Used for logging
    private static final String TAG = "DATABASE_MESSAGE";

    //Static variables
    //Database version
    private static final int DATABASE_VERSION = 1;

    //Database name
    private static final String DATABASE_NAME = "songsManager";

    //Table names
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_ARTISTS = "artists";

    //Table columns' names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_ALBUM = "album";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating tables
    //Tutorial had ID as the primary key. Took that off since each song will originally
    //have its own ID when added to the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //The strings used for the queries
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + KEY_ID + " INTEGER," + KEY_TITLE + " TEXT,"
                + KEY_ARTIST + " TEXT," + KEY_ALBUM + " TEXT" + ")";
        String CREATE_ARTISTS_TABLE = "CREATE TABLE " + TABLE_ARTISTS + "("
                + KEY_ID + " INTEGER," + KEY_ARTIST + " TEXT" + ")";

        //Execute the strings
        db.execSQL(CREATE_SONGS_TABLE);
        db.execSQL(CREATE_ARTISTS_TABLE);
    }

    /**
     * Upgrading the database
     * @param db database to delete/upgrade
     * @param oldVersion old version
     * @param newVersion new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);

        //Create tables again
        onCreate(db);
    }

    /**
     * CRUD Operations
     * Not all operations are listed. Can add more if needed
     * Adding a new song
     * This is equivalent to adding a new record
     * @param song to add
     */
    public void addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, song.getID());
        values.put(KEY_TITLE, song.getTitle());
        values.put(KEY_ARTIST, song.getArtist());
        values.put(KEY_ALBUM, song.getAlbum());

        //Inserting row
        db.insert(TABLE_SONGS, null, values);
        db.close();
    }

    /**
     * Getting a single song
     * Might not use ID to get a song, but instead use the title. Change later
     * @param id used to identify the song to pick
     * @return song picked
     */
    public Song getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, new String[] {KEY_ID, KEY_TITLE, KEY_ARTIST, KEY_ALBUM},
                KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();

        Song song = new Song(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3));

        //Return song
        return song;
    }

    /**
     * THERE MIGHT BE A MORE EFFICIENT WAY OF DOING THIS!
     * This will return a list of the artists from the songs table
     * @return ArrayList list of the artists
     */
    public ArrayList<String> getAllArtists() {
        //The list that will hold the artists
        ArrayList<String> artistList = new ArrayList<String>();

        //The query for selecting the artists
        String selectQuery = "SELECT DISTINCT " + KEY_ARTIST + " FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop through all the rows and add to the list
        if(cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(0);
                //Add the artist to the list
                artistList.add(artist);
            } while(cursor.moveToNext());
        }

        //Sort the artists so that they are in alphabetical order
        Collections.sort(artistList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return artistList;
    }

    /**
     * This will return a list of the artists from the songs table
     * @return ArrayList list of the artists
     */
    public ArrayList<String> getAllAlbums() {
        //The list that will hold the artists
        ArrayList<String> albumsList = new ArrayList<String>();

        //The query for selecting the artists
        String selectQuery = "SELECT DISTINCT " + KEY_ALBUM + " FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop through all the rows and add to the list
        if(cursor.moveToFirst()) {
            do {
                String album = cursor.getString(0);
                //Add the artist to the list
                albumsList.add(album);
            } while(cursor.moveToNext());
        }

        //Sort the artists so that they are in alphabetical order
        Collections.sort(albumsList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return albumsList;
    }

    /**
     * Getting all the songs as a list
     * This is what will be used to make the listview for the songs
     * @return Arraylist list of songs
     */
    public ArrayList<Song> getAllSongs() {
        //The list that will hold the songs
        ArrayList<Song> songList = new ArrayList<Song>();

        //The query for selecting all
        String selectQuery = "SELECT * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop through all the rows and add to the list
        if(cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setAlbum(cursor.getString(3));
                //Add the song to the list
                songList.add(song);
                Log.v(TAG, song.getID() + " " + song.getTitle() + " retrieved from database.");
            } while(cursor.moveToNext());
        }

        /*It seems we do not need this, but it will stay just in case
        //Sort the data so songs are sorted alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });*/

        //Return the list
        return songList;
    }

    /**
     * Get songs count / total number of songs
     * @return int total number of songs
     */
    public int getSongsCount() {
        String countQuery = "SELECT * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        //Return the total count
        return cursor.getCount();
    }

    /**
     * Search through the database for the query that is sent to it
     * @param searchQuery the query to search for in the database
     * @return ArrayList with the search results
     */
    public ArrayList<Song> searchDatabase(String searchQuery) {
        Log.v(TAG, "Started searching the database");

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> searchResults = new ArrayList<Song>();

        String databaseQuery = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_TITLE + " LIKE " +
                "'%" + searchQuery + "%'";

        Cursor cursor = db.rawQuery(databaseQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setAlbum(cursor.getString(3));
                searchResults.add(song);
                Log.v(TAG, song.getTitle() + " matched the search query");
            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    /**
     * This will return the songs that are associated with the artist picked
     * @param searchQuery the artist name
     * @return ArrayList of songs matching the artist
     */
    public ArrayList<Song> searchArtistPickedSongs(String searchQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> searchResults = new ArrayList<Song>();

        String databaseQuery = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_ARTIST + " = '" +
                searchQuery + "'";

        Cursor cursor = db.rawQuery(databaseQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setAlbum(cursor.getString(3));
                searchResults.add(song);
                Log.v(TAG, song.getTitle() + " matched the artist query");
            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    /**
     * This will return the songs that are associated with the album picked
     * @param searchQuery the album name
     * @return ArrayList of songs matching the album
     */
    public ArrayList<Song> searchAlbumPickedSongs(String searchQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> searchResults = new ArrayList<Song>();

        String databaseQuery = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_ALBUM + " = '" +
                searchQuery + "'";

        Cursor cursor = db.rawQuery(databaseQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setAlbum(cursor.getString(3));
                searchResults.add(song);
                Log.v(TAG, song.getTitle() + " matched the album query");
            }while(cursor.moveToNext());
        }

        return searchResults;
    }
}