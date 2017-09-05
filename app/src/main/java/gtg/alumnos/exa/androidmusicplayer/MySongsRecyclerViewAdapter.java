package gtg.alumnos.exa.androidmusicplayer;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
public class MySongsRecyclerViewAdapter extends RecyclerView.Adapter<MySongsRecyclerViewAdapter.ViewHolder> {

    private final List<Quart<String, String, Long, String>> mValues;
    private final SongsFragment.OnListFragmentSongInteractionListener mListener;

    public MySongsRecyclerViewAdapter(List<Quart<String, String, Long, String>> items, SongsFragment.OnListFragmentSongInteractionListener listener) {
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

        if (holder.mItem.first != null) {
            holder.title.setText(holder.mItem.first);
        }

        if (holder.mItem.third != null) {
            holder.duration.setText(DateUtils.formatElapsedTime(holder.mItem.third / 1000l));
        }

        if (holder.mItem.fourth != null) {
            holder.art.setImageURI(Uri.parse(holder.mItem.fourth));
        } else {
            //holder.art.setVisibility(View.GONE);
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
        public final TextView duration;
        public final ImageView art;
        public Quart<String, String, Long, String> mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            duration = (TextView) view.findViewById(R.id.duration);
            art = (ImageView) view.findViewById(R.id.art);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
