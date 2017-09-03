package gtg.alumnos.exa.androidmusicplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentAlbumInteractionListener}
 * interface.
 */
public class SongsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SELECTION = "selection";
    private static final String ARG_SELECTIONARGS = "slelection-args";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    RecyclerView recyclerView;
    private List<Quart<String, String, String, String>> mData;
    private OnListFragmentAlbumInteractionListener mListener;
    private MyAlbumsRecyclerViewAdapter adapter;
    private String[] selectionArgs;
    private String selection;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongsFragment() {
        this.mData = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SongsFragment newInstance(int columnCount, String selection, String[] selectionArgs) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_SELECTION, selection);
        args.putStringArray(ARG_SELECTIONARGS, selectionArgs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            selection = getArguments().getString(ARG_SELECTION);
            selectionArgs = getArguments().getStringArray(ARG_SELECTIONARGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_list, container, false);
        getLoaderManager().initLoader(0, null, new AlbumCursorLoaderCB());
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
           recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyAlbumsRecyclerViewAdapter(mData, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentAlbumInteractionListener) {
            mListener = (OnListFragmentAlbumInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentAlbumInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentAlbumInteraction(Quart<String, String, String, String> item);
    }

    protected class AlbumCursorLoaderCB implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(SongsFragment.this.getActivity(),
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{"_id",MediaStore.Audio.AlbumColumns.ARTIST, MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS, MediaStore.Audio.AlbumColumns.ALBUM_ART}, SongsFragment.this.selection, SongsFragment.this.selectionArgs, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            int index;
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String songs_count = null;
                String album = null;
                String artist = null;
                String thumbnail = null;

                if ((index = data.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)) != -1)
                    artist = data.getString(index);

                if ((index = data.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM)) != -1)
                    album = data.getString(index);

                if ((index = data.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS)) != -1)
                    songs_count = String.valueOf(data.getInt(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART)) != -1)
                    thumbnail = data.getString(index);

                SongsFragment.this.mData.add(new Quart<String, String, String, String>(artist, album, songs_count, thumbnail));
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            SongsFragment.this.mData.clear();
        }
    }
}
