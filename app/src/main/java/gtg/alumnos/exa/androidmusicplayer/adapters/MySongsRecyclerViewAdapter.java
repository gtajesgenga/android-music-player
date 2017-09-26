package gtg.alumnos.exa.androidmusicplayer.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.fragments.AlbumsFragment.OnListFragmentAlbumInteractionListener;
import gtg.alumnos.exa.androidmusicplayer.fragments.SongsFragment;
import gtg.alumnos.exa.androidmusicplayer.models.Song;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Song} and makes a call to the
 * specified {@link OnListFragmentAlbumInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongsRecyclerViewAdapter extends RecyclerView.Adapter<MySongsRecyclerViewAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final SongsFragment.OnListFragmentSongInteractionListener mListener;

    public MySongsRecyclerViewAdapter(List<Song> items, SongsFragment.OnListFragmentSongInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem.getTitle() != null) {
            holder.title.setText(holder.mItem.getTitle());
        }

        if (holder.mItem.getArtist() != null) {
            holder.artist.setText(holder.mItem.getArtist());
        }

        if (holder.mItem.getAlbum() != null) {
            holder.album.setText(holder.mItem.getAlbum());
        }

        if (holder.mItem.getFormatedDuration() != null) {
            holder.duration.setText(holder.mItem.getFormatedDuration());
        }

        if (holder.mItem.getAlbumArt() != null) {
            holder.art.setImageURI(Uri.parse(holder.mItem.getAlbumArt()));
        } else {
            holder.art.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentSongInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView album;
        public final TextView artist;
        public final TextView duration;
        public final ImageView art;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            duration = (TextView) view.findViewById(R.id.duration);
            album = (TextView) view.findViewById(R.id.album);
            artist = (TextView) view.findViewById(R.id.artist);
            art = (ImageView) view.findViewById(R.id.art);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
