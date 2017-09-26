package gtg.alumnos.exa.androidmusicplayer.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.fragments.PlaylistsFragment;
import gtg.alumnos.exa.androidmusicplayer.models.Playlist;
import gtg.alumnos.exa.androidmusicplayer.models.Song;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtil;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Playlist} and makes a call to the
 * specified {@link PlaylistsFragment.OnListFragmentPlaylistsInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlaylistRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaylistRecyclerViewAdapter.ViewHolder> {

    private final List<Playlist> mValues;
    private final PlaylistsFragment.OnListFragmentPlaylistsInteractionListener mListener;

    public MyPlaylistRecyclerViewAdapter(List<Playlist> items, PlaylistsFragment.OnListFragmentPlaylistsInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(holder.mItem.getName());
        holder.songs.setText(String.format(holder.mView.getResources().getString(R.string.count_songs), String.valueOf(holder.mItem.getCount())));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentPlaylistInteraction(holder.mItem);
                }
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, holder.mItem);
            }
        });
    }

    private void showPopupMenu(View view, Playlist playlist) {
        // inflate menu
        PopupMenu popup = new PopupMenu((Context) mListener, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, popup.getMenu());
        popup.getMenu().findItem(R.id.action_add_playlist).setVisible(false);
        popup.setOnMenuItemClickListener(new MyPlaylistRecyclerViewAdapter.MyMenuItemClickListener(playlist));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int position) {
        Playlist item = mValues.remove(position);
        if (item != null) {
            StorageUtil st = new StorageUtil((Context) mListener);
            st.removePlaylist(item);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mValues.size());
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private final List<Song> list;

        public MyMenuItemClickListener(Playlist playlist) {
            this.list = playlist.getSongs();
        }

        /**
         * This method will be invoked when a menu item is clicked if the item itself did
         * not already handle the event.
         *
         * @param menuItem {@link MenuItem} that was clicked
         * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
         */
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_play_all:
                    mListener.onOverflowPlaylistInteraction((ArrayList<Song>) list);
                    return true;
                default:
            }
            return false;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name;
        public final TextView songs;
        public final ImageView overflow;
        public Playlist mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.name);
            songs = (TextView) view.findViewById(R.id.songs);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
