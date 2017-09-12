package gtg.alumnos.exa.androidmusicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SELECTION = "selection";
    private static final String ARG_SELECTIONARGS = "selection-args";
    private ImageView art;
    private ImageButton playPause;
    private ImageButton prev;
    private ImageButton next;
    private RecyclerView recyclerView;
    private int mColumnCount = 1;
    private List<Song> mData;
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter adapter;
    private String[] selectionArgs;
    private String selection;
    private String artist;
    private String data;
    private String album_id;
    private String albumArt;
    private boolean playing;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().endsWith(PlayerService.CLOSE_OK)){
                ItemFragment.this.getActivity().onBackPressed();
                return;
            }

            if(intent.getAction().endsWith(PlayerService.SEND_LIST)){
                mData = (List<Song>) intent.getSerializableExtra(PlayerService.EXTRA_LIST);
                adapter = new MyItemRecyclerViewAdapter(mData, mListener);
                recyclerView.setAdapter(adapter);
                return;
            }
            playing = intent.getBooleanExtra(PlayerService.EXTRA_PLAYING,false);
            if(playing) {
                playPause.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                playPause.setImageResource(android.R.drawable.ic_media_play);
            }
            Song s = (Song) intent.getSerializableExtra(PlayerService.EXTRA_SONG);
            if (s.getAlbumArt() != null) {
                art.setImageURI(Uri.parse(s.getAlbumArt()));
                art.setVisibility(View.VISIBLE);
            } else
                art.setVisibility(View.INVISIBLE);
            for (Song item : mData) {
                if (!item.equals(s))
                    item.setSongStatus(Song.SongStatus.STOPED);
            }
            mData.get(mData.indexOf(s)).setSongStatus(s.getSongStatus());
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
        mData = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount, String selection, String[] selectionArgs) {
        ItemFragment fragment = new ItemFragment();
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

        if(this.localBroadcastManager==null) {
            this.localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
        }

        for (int i = 0; i < selection.split(",").length; i++) {
            switch (selection.split(",")[i]) {
                case MediaStore.Audio.Media.ARTIST:
                    artist = selectionArgs[i];
                    break;
                case MediaStore.Audio.Media.ALBUM_ID:
                    album_id = selectionArgs[i];
                    break;
                case MediaStore.Audio.AlbumColumns.ALBUM_ART:
                    albumArt = selectionArgs[i];
                    break;
                case MediaStore.Audio.Media.DATA:
                    data = selectionArgs[i];
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        art = (ImageView) view.findViewById(R.id.art);

        playPause = (ImageButton) view.findViewById(R.id.btn_play);
        prev = (ImageButton) view.findViewById(R.id.btn_prev);
        next = (ImageButton) view.findViewById(R.id.btn_next);

        this.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if(playing)
                    i.setAction(PlayerService.PAUSE_PLAYING);
                else
                    i.setAction(PlayerService.START_PLAYING);
                localBroadcastManager.sendBroadcast(i);
            }
        });

        this.prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localBroadcastManager.sendBroadcast(
                        new Intent(PlayerService.PREV_SONG));
            }
        });

        this.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localBroadcastManager.sendBroadcast(
                        new Intent(PlayerService.NEXT_SONG));
            }
        });

        // Set the adapter
        if (view.findViewById(R.id.list) != null && view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        Intent intent = new Intent(this.getActivity(),  PlayerService.class);
        intent.putExtra(MediaStore.Audio.Media.ARTIST, artist);
        intent.putExtra(MediaStore.Audio.Media.ALBUM_ID, album_id);
        intent.putExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART, albumArt);
        intent.putExtra(MediaStore.Audio.Media.DATA, data);
        this.getActivity().startService(intent);

        return view;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter =new IntentFilter(PlayerService.PLAYING_INFO);
        intentFilter.addAction(PlayerService.CLOSE_OK);
        intentFilter.addAction(PlayerService.SEND_LIST);
        if(this.localBroadcastManager!= null)
            this.localBroadcastManager.registerReceiver(this.broadcastReceiver,
                    intentFilter);
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Song item);
    }

    protected class SongCursorLoaderCB implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(ItemFragment.this.getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{"_id",MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID}, ItemFragment.this.selection, ItemFragment.this.selectionArgs, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            ItemFragment.this.mData.clear();
            int index;
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                Song song = new Song();

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.TITLE)) != -1)
                    song.setTitle(data.getString(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.DATA)) != -1)
                    song.setUri(data.getString(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.DURATION)) != -1)
                    song.setDuration(data.getLong(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.ARTIST)) != -1)
                    song.setArtist(data.getString(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)) != -1)
                    song.setArtist_id(data.getLong(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.ALBUM)) != -1)
                    song.setAlbum(data.getString(index));

                if ((index = data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) != -1)
                    song.setAlbum_id(data.getLong(index));

                Cursor cursor = ItemFragment.this.getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID+ "=?",
                        new String[] {String.valueOf(song.getAlbum_id())},
                        null);

                if (cursor.moveToFirst()) {
                    song.setAlbumArt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                }
                cursor.close();

                ItemFragment.this.mData.add(song);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ItemFragment.this.mData.clear();
        }
    }

}
