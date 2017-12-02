package gtg.alumnos.exa.androidmusicplayer.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gus on 23/09/17.
 */

public class Playlist implements Comparable, Serializable {

    private String name;
    private List<Song> songs;

    @Override
    public int compareTo(@NonNull Object o) {
        return this.name.compareTo(((Playlist) o).getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !getClass().equals(o.getClass()))
            return false;

        Playlist obj = (Playlist) o;

        return obj.getName().trim().equals(this.getName().trim());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public int getCount() {
        return songs.size();
    }
}
