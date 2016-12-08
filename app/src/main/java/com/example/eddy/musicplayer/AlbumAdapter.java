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
 * Created by e-sal on 12/6/2016.
 */

public class AlbumAdapter extends BaseAdapter {
    //Variables
    private ArrayList<String> albums;
    private LayoutInflater albumInflater;

    //Constructor used to instantiate variables
    public AlbumAdapter(Context c, ArrayList<String> theAlbums) {
        albums = theAlbums;
        albumInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Map to album layout
        LinearLayout albumLayout = (LinearLayout) albumInflater.inflate(R.layout.album, parent, false);
        //Get album view
        TextView albumView = (TextView) albumLayout.findViewById(R.id.albumName);
        //Get album using position
        String currentAlbum = albums.get(position);
        //Get album string
        albumView.setText(currentAlbum);
        //set position as tag
        albumLayout.setTag(currentAlbum);
        return albumLayout;
    }
}
