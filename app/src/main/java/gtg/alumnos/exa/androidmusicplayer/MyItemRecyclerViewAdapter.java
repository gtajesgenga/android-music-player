package gtg.alumnos.exa.androidmusicplayer;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gtg.alumnos.exa.androidmusicplayer.ItemFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Song} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Song> items, OnListFragmentInteractionListener listener) {
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

        if (holder.mItem.getFormatedDuration() != null) {
            holder.duration.setText(holder.mItem.getFormatedDuration());
        }

        if (holder.mItem.getAlbumArt() != null) {
            holder.art.setImageURI(Uri.parse(holder.mItem.getAlbumArt()));
        } else {
            holder.art.setVisibility(View.GONE);
        }

        holder.flag.setVisibility(View.VISIBLE);
        if (holder.mItem.getIs_playing().equals(Song.SongStatus.PLAYING)) {
            holder.flag.setImageDrawable(holder.mView.getResources().getDrawable(R.drawable.ic_play_circle_filled_lightblue, holder.mView.getContext().getTheme()));
        } else if (holder.mItem.getIs_playing().equals(Song.SongStatus.PAUSED)) {
            holder.flag.setImageDrawable(holder.mView.getResources().getDrawable(R.drawable.ic_pause_circle_filled_lightblue, holder.mView.getContext().getTheme()));
        } else {
            holder.flag.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final FloatingActionButton flag;

        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            duration = (TextView) view.findViewById(R.id.duration);
            art = (ImageView) view.findViewById(R.id.art);
            flag = (FloatingActionButton) view.findViewById(R.id.play_flag);
            flag.setBackgroundTintList(null);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
