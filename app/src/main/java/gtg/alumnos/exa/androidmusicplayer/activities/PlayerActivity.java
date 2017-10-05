package gtg.alumnos.exa.androidmusicplayer.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import gtg.alumnos.exa.androidmusicplayer.CustomTouchListener;
import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.adapters.MyRecyclerViewAdapter;
import gtg.alumnos.exa.androidmusicplayer.fragments.LyricDialogFragment;
import gtg.alumnos.exa.androidmusicplayer.models.Song;
import gtg.alumnos.exa.androidmusicplayer.onItemClickListener;
import gtg.alumnos.exa.androidmusicplayer.services.MediaPlayerService;
import gtg.alumnos.exa.androidmusicplayer.utils.StorageUtil;

public class PlayerActivity extends AppCompatActivity implements LyricDialogFragment.NoticeDialogListener {

    public static final String Broadcast_PLAY_NEW_AUDIO =
            "gtg.alumnos.exa.androidmusicplayer.PlayNewAudio";
    public static final String Broadcast_LYRICS =
            "gtg.alumnos.exa.androidmusicplayer.Lyrics";
    public static final String SELECTION = "selection";
    public static final String SELECTION_ARGS = "selectionArgs";
    public static final String ALBUM_ART = "albumArt";
    public static final String SONGS_LIST = "songs_list";
    public static final String LYRIC = "lyric";
    boolean serviceBound = false;
    ImageView collapsingImageView;
    int imageIndex = 0;
    private MediaPlayerService player;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String lyric = (String) intent.getSerializableExtra(LYRIC);
            LyricDialogFragment fragment = LyricDialogFragment.newInstance(PlayerActivity.this, lyric);
            fragment.show(getSupportFragmentManager(), "Dialog");
        }
    };
    private ArrayList<Song> audioList;
    private String selection;
    private String[] selectionArgs;
    private String albumArt;
    private long seek;
    private Integer audioIndex = 0;
    private Long audioSeek = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);
        collapsingImageView = (ImageView) findViewById(R.id.collapsingImageView);

        registerReceiver(broadcastReceiver, new IntentFilter(PlayerActivity.Broadcast_LYRICS));

        audioList = null;

        Intent i = getIntent();

        if (i != null) {
            selection = i.getStringExtra(SELECTION);
            selectionArgs = i.getStringArrayExtra(SELECTION_ARGS);
            albumArt = i.getStringExtra(ALBUM_ART);
            audioList = (ArrayList<Song>) i.getSerializableExtra(SONGS_LIST);
        }

        if (albumArt == null) {
            loadCollapsingImage(imageIndex);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageIndex == 4) {
                        imageIndex = 0;
                        loadCollapsingImage(imageIndex);
                    } else {
                        loadCollapsingImage(++imageIndex);
                    }
                }
            });
        } else
            collapsingImageView.setImageURI(Uri.parse(albumArt));

        if (audioList == null) {
            if (selection != null)
                loadAudio();
            else {
                StorageUtil st = new StorageUtil(this);
                audioList = st.loadQueue();
                Pair<Integer, Long> positions = st.loadPositions();
                if (positions != null) {
                    audioIndex = positions.first;
                    audioSeek = positions.second;
                }
            }
        }

        if (audioList != null && !audioList.isEmpty()) {
            initRecyclerView();
            playAudio(audioIndex, audioSeek);
        }

    }

    private void initRecyclerView() {
        if (audioList.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(audioList, getApplication());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    playAudio(index);
                }
            }));

        }
    }


    private void loadCollapsingImage(int i) {
        TypedArray array = getResources().obtainTypedArray(R.array.images);
        collapsingImageView.setImageDrawable(array.getDrawable(i));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    private void playAudio(int audioIndex) {
        playAudio(audioIndex, 0);
    }

    private void playAudio(int audioIndex, long seek) {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("audioIndex", audioIndex);
            playerIntent.putExtra("audioSeek", seek);
            playerIntent.putExtra("audioList", audioList);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("audioIndex", audioIndex);
            broadcastIntent.putExtra("audioSeek", seek);
            sendBroadcast(broadcastIntent);
        }
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0" +
                ((PlayerActivity.this.selection != null) ? " AND " + PlayerActivity.this.selection : "");
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, PlayerActivity.this.selectionArgs, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                // Save to audioList
                audioList.add(new Song(title, artist, album, data, albumArt, duration));
            }
        }
        cursor.close();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }
}
