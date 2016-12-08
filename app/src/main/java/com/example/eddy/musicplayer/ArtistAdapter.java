package com.example.eddy.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by e-sal on 12/3/2016.
 */

public class ArtistAdapter extends BaseAdapter{
    //Variables
    private ArrayList<String> artists;
    private LayoutInflater artistInflater;

    //Constructor used to instantiate variables
    public ArtistAdapter(Context c, ArrayList<String> theArtists) {
        artists = theArtists;
        artistInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Map to artist layout
        LinearLayout artistLayout = (LinearLayout) artistInflater.inflate(R.layout.artist, parent, false);
        //Get artist view
        TextView artistView = (TextView) artistLayout.findViewById(R.id.artistName);
        //Get artist using position
        String currentArtist = artists.get(position);
        //Get artist string
        artistView.setText(currentArtist);
        //set position as tag
        artistLayout.setTag(currentArtist);
        return artistLayout;
    }
}
