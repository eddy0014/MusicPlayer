package com.example.eddy.musicplayer;

public class Song {

    //Variables for the data for each track
    private int id;
    private String title;
    private String artist;
    private String album;

    //Empty constructor
    public Song() {
    }

    //Constructor used to instantiate the variables
    public Song(int songID, String songTitle, String songArtist, String songAlbum) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        album = songAlbum;
    }

    //Getters for the variables
    public int getID() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }
    public String getAlbum() {
        return album;
    }

    //Setters for the variables
    public void setID(int id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setArtist(String artist) {this.artist = artist;}
    public void setAlbum(String album) {
        this.album = album;
    }
}