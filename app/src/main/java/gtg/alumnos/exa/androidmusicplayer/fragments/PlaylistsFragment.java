package gtg.alumnos.exa.androidmusicplayer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.adapters.MyPlaylistRecyclerViewAdapter;
import gtg.alumnos.exa.androidmusicplayer.models.Playlist;
import gtg.alumnos.exa.androidmusicplayer.models.Song;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtil;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtilLoader;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentPlaylistsInteractionListener}
 * interface.
 */
public class PlaylistsFragment extends Fragment implements PlaylistDialogFragment.NoticeDialogListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private List<Playlist> mData;
    private int mColumnCount = 1;
    private OnListFragmentPlaylistsInteractionListener mListener;
    private MyPlaylistRecyclerViewAdapter adapter;
    private PlaylistLoaderCB loader;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaylistsFragment() {
        this.mData = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlaylistsFragment newInstance(int columnCount) {
        PlaylistsFragment fragment = new PlaylistsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.removeItem(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.colorAccent));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setSubtitle(getActivity().getResources().getString(R.string.playlist));
        View view = inflater.inflate(R.layout.fragment_playlist_list, container, false);

        if (view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyPlaylistRecyclerViewAdapter(mData, mListener);
            recyclerView.setAdapter(adapter);
            loader = new PlaylistLoaderCB();
            getLoaderManager().initLoader(0, null, loader);

            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment dialog = PlaylistDialogFragment.newInstance(PlaylistsFragment.this);
                    dialog.show(getFragmentManager(), "Dialog");
                }
            });
        }
        initSwipe();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentPlaylistsInteractionListener) {
            mListener = (OnListFragmentPlaylistsInteractionListener) context;
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        getLoaderManager().restartLoader(0, null, loader);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
    public interface OnListFragmentPlaylistsInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentPlaylistInteraction(Playlist item);

        void onOverflowPlaylistInteraction(ArrayList<Song> list);
    }

    private class PlaylistLoaderCB implements LoaderManager.LoaderCallbacks<StorageUtil> {
        @Override
        public Loader<StorageUtil> onCreateLoader(int i, Bundle bundle) {
            return new StorageUtilLoader(PlaylistsFragment.this.getActivity());
        }

        @Override
        public void onLoadFinished(Loader<StorageUtil> loader, StorageUtil o) {
            PlaylistsFragment.this.mData.clear();
            PlaylistsFragment.this.mData = o.loadPlaylists() != null ? o.loadPlaylists() : mData;
            adapter = new MyPlaylistRecyclerViewAdapter(mData, mListener);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<StorageUtil> loader) {
            PlaylistsFragment.this.mData.clear();
        }
    }
}
