package gtg.alumnos.exa.androidmusicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.models.Playlist;
import gtg.alumnos.exa.androidmusicplayer.models.Song;

/**
 * Created by gus on 23/09/17.
 */

public class StorageUtil {
    private static final String PLAYLISTS = "gtg.alumnos.exa.androidmusicplayer.PLAYLISTS";
    private final String STORAGE = "gtg.alumnos.exa.androidmusicplayer.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storePlaylists(ArrayList<Playlist> list) {
        Collections.sort(list);
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(PLAYLISTS, json);
        editor.apply();
    }

    public void addPlaylist(Playlist p) {
        ArrayList<Playlist> list = loadPlaylists();
        if (list == null)
            list = new ArrayList<>();

        list.add(p);
        storePlaylists(list);
    }

    public void removePlaylist(Playlist p) {
        ArrayList<Playlist> list = loadPlaylists();
        if (list != null) {
            list.remove(p);
            storePlaylists(list);
        }
    }

    public void clearPlaylists() {
        storePlaylists(new ArrayList<Playlist>());
    }

    public ArrayList<Playlist> loadPlaylists() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(PLAYLISTS, null);
        Type type = new TypeToken<ArrayList<Playlist>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void insertOnPlaylist(CharSequence name, List<Song> songs) {
        ArrayList<Playlist> list = loadPlaylists();
        Playlist aux = new Playlist();
        aux.setName(name.toString());
        aux.setSongs(new ArrayList<Song>());
        for (Playlist p :
                list) {
            if (p.getName().equals(name.toString())) {
                p.getSongs().addAll(songs);
                storePlaylists(list);
                return;
            }
        }
    }
}

