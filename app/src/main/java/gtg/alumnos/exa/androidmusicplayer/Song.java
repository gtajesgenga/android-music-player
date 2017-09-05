package gtg.alumnos.exa.androidmusicplayer;

import android.text.format.DateUtils;

/**
 * Created by gus on 05/09/17.
 */

public class Song {

    private String title;
    private Long duration;
    private String Artist;
    private String album;
    private String uri;

    public String getAlbumArt() {
        return albumArt;
    }

    private String albumArt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public String getFormatedDuration() {
        return DateUtils.formatElapsedTime(getDuration() / 1000l);
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}
