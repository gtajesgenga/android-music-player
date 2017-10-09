package gtg.alumnos.exa.androidmusicplayer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import gtg.alumnos.exa.androidmusicplayer.R;

/**
 * Created by gus on 04/10/17.
 */

public class LyricDialogFragment extends DialogFragment {

    private NoticeDialogListener mListener;
    private String lyric;

    public static LyricDialogFragment newInstance(LyricDialogFragment.NoticeDialogListener listener, String lyric) {
        LyricDialogFragment fragment = new LyricDialogFragment();
        fragment.setmListener(listener);
        fragment.setLyric(lyric);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.lyric_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.lyric);
        textView.setText(lyric);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lyrics_dialog_title);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(LyricDialogFragment.this);
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setmListener(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }
}
