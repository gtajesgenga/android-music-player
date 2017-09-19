package gtg.alumnos.exa.androidmusicplayer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gus on 13/09/17.
 */

public class MyItemFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SELECTION = "selection";
    private static final String ARG_SELECTIONARGS = "selection-args";

    public static final String Broadcast_PLAY_NEW_AUDIO = "gtg.alumnos.exa.androidmusicplayer.PlayNewAudio";
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
    private MyService player;
    private boolean serviceBound = false;
    private int audioIndex = -1;
    private Song activeSong;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyItemFragment() {
        mData = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public static MyItemFragment newInstance(int columnCount, String selection, String[] selectionArgs) {
        MyItemFragment fragment = new MyItemFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        getLoaderManager().initLoader(0, null, new ItemsCursorLoaderCB());
        art = (ImageView) view.findViewById(R.id.art);
        playPause = (ImageButton) view.findViewById(R.id.btn_play);
        prev = (ImageButton) view.findViewById(R.id.btn_prev);
        next = (ImageButton) view.findViewById(R.id.btn_next);

        // Set the adapter
        if (view.findViewById(R.id.list) != null && view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyItemRecyclerViewAdapter(mData, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            this.getActivity().unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param savedInstanceState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
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
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            player = binder.getService();
            player.setActiveSong(activeSong);
            serviceBound = true;

            Toast.makeText(MyItemFragment.this.getActivity(), "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) player.getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(player.getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                player.stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeFile(activeSong.getAlbumArt());
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeSong.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeSong.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeSong.getTitle())
                .build());
    }


    private void playAudio(int audioIndex) {
        if (audioIndex < 0 || audioIndex >= mData.size())
            return;

        activeSong = mData.get(audioIndex);
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this.getActivity(), MyService.class);
            playerIntent.putExtra("media", activeSong.getUri());
            this.getActivity().startService(playerIntent);
            this.getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("media", activeSong.getUri());
            sendBroadcast(broadcastIntent);
        }
    }

    private void skipToNext() {
        playAudio(++audioIndex);
    }

    private void skipToPrevious() {
        playAudio(--audioIndex);
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeFile(activeSong.getAlbumArt()); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this.getActivity())
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeSong.getArtist())
                .setContentTitle(activeSong.getAlbum())
                .setContentInfo(activeSong.getTitle())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) player.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) player.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this.getActivity(), MyService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(player.ACTION_PLAY);
                return PendingIntent.getService(player, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(player.ACTION_PAUSE);
                return PendingIntent.getService(player, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(player.ACTION_NEXT);
                return PendingIntent.getService(player, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(player.ACTION_PREVIOUS);
                return PendingIntent.getService(player, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
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

    private class ItemsCursorLoaderCB implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MyItemFragment.this.getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{"_id", MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID},
                    selection, selectionArgs, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            MyItemFragment.this.mData.clear();
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                Song s = new Song();
                s.setUri(data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA)));
                s.setTitle(data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                s.setDuration(data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                s.setArtist(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                s.setAlbum_id(data.getLong(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                s.setAlbum(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                Cursor albumCursor = MyItemFragment.this.getActivity().getContentResolver().query(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID + " = ?",
                        new String[]{Long.toString(data.getLong(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))},
                        null);
                while (albumCursor.moveToNext()) {
                    s.setAlbumArt(albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                }
                albumCursor.close();
                MyItemFragment.this.mData.add(s);
            }
            data.close();
            if (!MyItemFragment.this.mData.isEmpty()) {
                audioIndex = 0;
                playAudio(audioIndex);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            MyItemFragment.this.mData.clear();
        }
    }
}
