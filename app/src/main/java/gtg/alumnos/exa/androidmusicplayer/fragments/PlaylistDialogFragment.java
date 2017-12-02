package gtg.alumnos.exa.androidmusicplayer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import java.util.ArrayList;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.models.Playlist;
import gtg.alumnos.exa.androidmusicplayer.models.Song;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtil;

/**
 * Created by gus on 23/09/17.
 */

public class PlaylistDialogFragment extends DialogFragment {

    NoticeDialogListener mListener;

    public static PlaylistDialogFragment newInstance(NoticeDialogListener listener) {
        PlaylistDialogFragment fragment = new PlaylistDialogFragment();
        fragment.setmListener(listener);
        return fragment;
    }

    public NoticeDialogListener getmListener() {
        return mListener;
    }

    public void setmListener(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.playlist_dialog_title)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Playlist p = new Playlist();
                        p.setName(input.getText().toString());
                        p.setSongs(new ArrayList<Song>());
                        StorageUtil st = new StorageUtil(getActivity());
                        st.addPlaylist(p);
                        mListener.onDialogPositiveClick(PlaylistDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(PlaylistDialogFragment.this);
                    }
                });

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        return builder.create();
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }

}
