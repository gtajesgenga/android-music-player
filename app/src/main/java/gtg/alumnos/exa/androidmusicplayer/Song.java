package gtg.alumnos.exa.androidmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.io.Serializable;

/**
 * Created by gus on 05/09/17.
 */

public class Song implements Serializable {

    public enum SongStatus {
        PLAYING,
        PAUSED,
        STOPED
    }

    private String title;
    private Long duration;
    private String Artist;
    private String album;
    private Long album_id;
    private Long artist_id;
    private String uri;
    private String albumArt;

    private SongStatus is_playing = SongStatus.STOPED;

    public Song() {}

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

    public SongStatus getIs_playing() {
        return is_playing;
    }

    public void setIs_playing(SongStatus is_playing) {
        this.is_playing = is_playing;
    }

    @Override
    public String toString() {
        return String.format("%s", this.title);
    }
}
