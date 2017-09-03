package gtg.alumnos.exa.androidmusicplayer;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.AlbumsFragment.OnListFragmentAlbumInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Quart<String, String, String, String>} and makes a call to the
 * specified {@link OnListFragmentAlbumInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAlbumsRecyclerViewAdapter extends RecyclerView.Adapter<MyAlbumsRecyclerViewAdapter.ViewHolder> {

    private final List<Quart<String, String, String, String>> mValues;
    private final OnListFragmentAlbumInteractionListener mListener;

    public MyAlbumsRecyclerViewAdapter(List<Quart<String, String, String, String>> items, OnListFragmentAlbumInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_albums_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem.first != null) {
            holder.artist.setText(holder.mItem.first);
        }

        if (holder.mItem.second != null) {
            holder.album.setText(holder.mItem.second);
        }

        if (holder.mItem.third != null) {
            holder.count.setText(String.format(holder.mView.getResources().getString(R.string.count_songs), holder.mItem.third));
        }

        if (holder.mItem.fourth != null) {
            holder.thumbnail.setImageURI(Uri.parse(holder.mItem.fourth));
        } else {
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentAlbumInteraction(holder.mItem);
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
        public final TextView artist;
        public final TextView album;
        public final ImageView overflow;
        public final ImageView thumbnail;
        public final TextView count;
        public Quart<String, String, String, String> mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            artist = (TextView) view.findViewById(R.id.artist);
            album = (TextView) view.findViewById(R.id.album);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            count = (TextView) view.findViewById(R.id.count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + album.getText() + "'";
        }
    }
}
