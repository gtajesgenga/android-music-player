package gtg.alumnos.exa.androidmusicplayer.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.fragments.ArtistsFragment.OnListFragmenArtistInteractionListener;
import gtg.alumnos.exa.androidmusicplayer.models.Artist;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pair<String,String>} and makes a call to the
 * specified {@link OnListFragmenArtistInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyArtistsRecyclerViewAdapter extends RecyclerView.Adapter<MyArtistsRecyclerViewAdapter.ViewHolder> {

    private final List<Artist> mValues;
    private final OnListFragmenArtistInteractionListener mListener;

    public MyArtistsRecyclerViewAdapter(List<Artist> items, OnListFragmenArtistInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artists_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        if (holder.mItem.getName() != null) {
            holder.artist.setText(holder.mItem.getName());
        }
        if (holder.mItem.getAlbums_count() != null) {
            holder.count.setText(String.format(holder.mView.getResources().getString(R.string.count_albums), holder.mItem.getAlbums_count().toString()));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentArtistInteraction(holder.mItem);
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

    private void showPopupMenu(View view, Artist artist) {
        // inflate menu
        PopupMenu popup = new PopupMenu((Context) mListener, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(artist));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private final Artist artist;

        public MyMenuItemClickListener(Artist artist) {
            this.artist = artist;
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
                    mListener.onOverflowArtistInteraction(artist);
                    return true;
                default:
            }
            return false;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final TextView artist;
        private final TextView count;
        private final ImageView overflow;
        public Artist mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            artist = (TextView) view.findViewById(R.id.artist);
            count = (TextView) view.findViewById(R.id.count);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + artist.getText() + "'";
        }
    }
}
