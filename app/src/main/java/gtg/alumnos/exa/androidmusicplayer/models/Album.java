package gtg.alumnos.exa.androidmusicplayer.models;

/**
 * Created by gus on 05/09/17.
 */

public class Album {

    private String artist;
    private String album;
    private Integer songs_count;
    private String album_art;
    private Integer id;

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

    public Integer getSongs_count() {
        return songs_count;
    }

    public void setSongs_count(Integer songs_count) {
        this.songs_count = songs_count;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
