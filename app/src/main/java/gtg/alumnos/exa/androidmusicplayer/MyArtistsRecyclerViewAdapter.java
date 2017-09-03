package gtg.alumnos.exa.androidmusicplayer;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gtg.alumnos.exa.androidmusicplayer.ArtistsFragment.OnListFragmenArtisttInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pair<String,String>} and makes a call to the
 * specified {@link OnListFragmenArtisttInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyArtistsRecyclerViewAdapter extends RecyclerView.Adapter<MyArtistsRecyclerViewAdapter.ViewHolder> {

    private final List<Pair<String,String>> mValues;
    private final OnListFragmenArtisttInteractionListener mListener;

    public MyArtistsRecyclerViewAdapter(List<Pair<String,String>> items, OnListFragmenArtisttInteractionListener listener) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (holder.mItem.first != null) {
            holder.artist.setText(holder.mItem.first);
        }
        if (holder.mItem.second != null) {
            holder.count.setText(String.format(holder.mView.getResources().getString(R.string.count_albums), holder.mItem.second));
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

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final TextView artist;
        private final TextView count;
        private final ImageView overflow;
        public Pair<String,String> mItem;

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
