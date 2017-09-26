package gtg.alumnos.exa.androidmusicplayer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Collections;
import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.models.Playlist;
import gtg.alumnos.exa.androidmusicplayer.models.Song;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtil;

/**
 * Created by gus on 23/09/17.
 */

public class PlaylistSelectorDialogFragment extends DialogFragment {

    private List<Song> songs;
    private StorageUtil st;
    private List<Playlist> playlists;
    private CharSequence[] names;

    public static PlaylistSelectorDialogFragment newInstance(List<Song> songs) {
        PlaylistSelectorDialogFragment fragment = new PlaylistSelectorDialogFragment();
        fragment.setSongs(songs);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        st = new StorageUtil(getContext());
        playlists = st.loadPlaylists();
        Collections.sort(playlists);
        names = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++)
            names[i] = playlists.get(i).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.playlist_dialog_selectortitle)
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        st.insertOnPlaylist(names[i], songs);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
