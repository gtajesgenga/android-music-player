package gtg.alumnos.exa.androidmusicplayer.models;

import android.text.format.DateUtils;

import java.io.Serializable;

/**
 * Created by gus on 05/09/17.
 */

public class Song implements Serializable {

    private String title;
    private Long duration;
    private String artist;
    private String album;
    private Long album_id;
    private Long artist_id;
    private String data;
    private String albumArt;

    public Song() {}

    public Song(String title, String artist, String album, String data, String albumArt, Long duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumArt = albumArt;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getFormatedDuration() {
        return DateUtils.formatElapsedTime(getDuration() / 1000l);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long album_id) {
        this.album_id = album_id;
    }

    public Long getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(Long artist_id) {
        this.artist_id = artist_id;
    }

    @Override
    public String toString() {
        return String.format("%s", this.title);
    }
}
