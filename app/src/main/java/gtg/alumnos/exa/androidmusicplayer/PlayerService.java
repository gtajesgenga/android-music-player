package gtg.alumnos.exa.androidmusicplayer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener  {

    private static final String TAG = PlayerService.class.getCanonicalName();
    public static final String PLAYING_INFO="gtg.alumnos.exa.androidmusicplayer.PLAYING_INFO";
    public static final String START_PLAYING="gtg.alumnos.exa.androidmusicplayer.START_PLAYING";
    public static final String PAUSE_PLAYING="gtg.alumnos.exa.androidmusicplayer.PAUSE_PLAYING";
    public static final String PREV_SONG="gtg.alumnos.exa.androidmusicplayer.PREV_SONG";
    public static final String NEXT_SONG="gtg.alumnos.exa.androidmusicplayer.NEXT_SONG";
    public static final String CLOSE_ ="gtg.alumnos.exa.androidmusicplayer.CLOSE";
    public static final String CLOSE_OK="gtg.alumnos.exa.androidmusicplayer.CLOSE_OK";
    public static final String EXTRA_PLAYING="playing";
    public static final String EXTRA_SONG="song";
    public static final String SEND_LIST = "gtg.alumnos.exa.androidmusicplayer.SEND_LIST";
    public static final String EXTRA_LIST = "list";

    private int index = 0;
    private ArrayList<Song> songList = new ArrayList<>();
    private boolean playing = false;
    private boolean pause = false;
    private String artist;
    private String album_id;
    private String data;
    private String albumArt;
    private MediaPlayer mediaPlayer;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver controlBroadcastReceiver;
    private BroadcastReceiver headsetBroadcastReceiver;


    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(index<songList.size()-1)
            PlayerService.this.nextSong();
        else {
            playing = false;
            index = 0;
            sendSongData();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.artist = intent.getStringExtra(MediaStore.Audio.Media.ARTIST);
        this.album_id = intent.getStringExtra(MediaStore.Audio.Media.ALBUM_ID);
        this.data = intent.getStringExtra(MediaStore.Audio.Media.DATA);
        this.albumArt = intent.getStringExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART);
        this.pause = false;
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        this.registerListeners();

        SongLoader sl = new SongLoader();
        sl.execute();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying Service");
        if(this.mediaPlayer.isPlaying())
            this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
        Intent i = new Intent(CLOSE_OK);
        this.localBroadcastManager.sendBroadcast(i);
        this.localBroadcastManager.unregisterReceiver(this.controlBroadcastReceiver);
        this.unregisterReceiver(this.headsetBroadcastReceiver);
        this.localBroadcastManager = null;
    }

    private void registerListeners() {
        if(localBroadcastManager!=null)
            return;
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //Control
        controlBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case START_PLAYING:
                        PlayerService.this.play();
                        break;
                    case PAUSE_PLAYING:
                        PlayerService.this.pause();
                        break;
                    case PREV_SONG:
                        PlayerService.this.prevSong();
                        break;
                    case NEXT_SONG:
                        PlayerService.this.nextSong();
                        break;
                    case CLOSE_:
                        PlayerService.this.close();
                        break;
                }
            }
        };

        IntentFilter intentF = new IntentFilter(START_PLAYING);
        intentF.addAction(PAUSE_PLAYING);
        intentF.addAction(PREV_SONG);
        intentF.addAction(NEXT_SONG);
        intentF.addAction(CLOSE_);
        this.localBroadcastManager.registerReceiver(controlBroadcastReceiver,
                intentF);

        this.headsetBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra("state",1);
                if(state==0)
                    PlayerService.this.pause();
            }
        };
        this.registerReceiver(this.headsetBroadcastReceiver,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    public void close() {
        Log.d(TAG, "Closing");
        pause();
        this.pause = false;
        stopForeground(true);
        this.stopSelf();
    }

    public void pause(){
        Log.d(TAG,"Pausing");
        if(playing) {
            playing = false;
            if(this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.pause();
                this.pause = true;
            }
            this.sendSongData();
        }
    }

    public void play(){
        Log.d(TAG, "Playing");
        if(this.pause){
            this.mediaPlayer.start();
            this.pause = false;
            this.playing = true;
            this.sendSongData();
            return;
        }
        if(this.index<this.songList.size()) {
            playing = true;
            Song s = this.songList.get(index);
            sendSongData();
            if(this.mediaPlayer.isPlaying())
                this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnCompletionListener(this);
            this.mediaPlayer.setOnPreparedListener(this);
            try {
                this.mediaPlayer.setDataSource(this, Uri.parse(s.getUri()));
                this.mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        Log.d(TAG,"Stoping");
        if(playing) {
            playing = false;
            pause = false;
            if(this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.sendSongData();
        }
    }

    public void prevSong(){
        Log.d(TAG,"Prev Song");
        if(index>0) {
            index--;
            this.pause = false;
            if(playing)
                this.play();
        }
    }


    public void nextSong(){
        Log.d(TAG,"Next Song");
        if(index<this.songList.size()-1) {
            index++;
            this.pause = false;
            if(playing)
                this.play();
        }
    }



    private class SongLoader extends AsyncTask<Object,Object,Object> {

        @Override
        protected Object doInBackground(Object... params) {
            //Cargando el cursor
            Log.d(TAG,"Creating Cursor");
            StringBuffer selection = new StringBuffer();
            StringBuffer selectionArgs = new StringBuffer();

            if (PlayerService.this.artist != null) {
                selection.append(",").append(MediaStore.Audio.Media.ARTIST).append("=?");
                selectionArgs.append(",").append(PlayerService.this.artist);
            }

            if (PlayerService.this.album_id != null) {
                selection.append(",").append(MediaStore.Audio.Media.ALBUM_ID).append("=?");
                selectionArgs.append(",").append(PlayerService.this.album_id);
            }

            if (PlayerService.this.data != null) {
                selection.append(",").append(MediaStore.Audio.Media.DATA).append("=?");
                selectionArgs.append(",").append(PlayerService.this.data);
            }

            if (selection.toString().startsWith(","))
                selection.replace(0, selection.length(),selection.toString().replaceFirst(",", ""));

            if (selectionArgs.toString().startsWith(","))
                selectionArgs.replace(0, selectionArgs.length(),selectionArgs.toString().replaceFirst(",", ""));

            Cursor data = PlayerService.this.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{"_id",MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION},
                    selection.toString().replace(",", " AND "),
                    selectionArgs.toString().split(","),
                    null);
            //Recorriendo el cursor
            PlayerService.this.songList.clear();
            PlayerService.this.index = 0;

            while(data.moveToNext()){
                Song s = new Song();
                s.setUri(data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA)));
                s.setTitle(data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                s.setDuration(data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                s.setArtist(PlayerService.this.artist);
                s.setAlbum_id(Long.valueOf(PlayerService.this.album_id));
                s.setAlbum(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                s.setAlbumArt(PlayerService.this.albumArt);
                PlayerService.this.songList.add(s);
            }

            Log.d(TAG,"Closing Cursor");
            data.close();

            Intent i = new Intent(SEND_LIST);
            i.putExtra(EXTRA_LIST, PlayerService.this.songList);
            PlayerService.this.localBroadcastManager.sendBroadcast(i);

            Log.d(TAG, "Loading Songs");
            PlayerService.this.stop();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d(TAG,"Songs Loaded: Playing");
            PlayerService.this.play();
        }
    }

    private void sendSongData() {
        Song s = this.songList.get(index);
        if (this.playing)
            s.setSongStatus(Song.SongStatus.PLAYING);
        else if (this.pause)
            s.setSongStatus(Song.SongStatus.PAUSED);
        else
            s.setSongStatus(Song.SongStatus.STOPED);
        //Foreground
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true);

        int drawable = android.R.drawable.ic_media_pause;
        if(playing)
            drawable = android.R.drawable.ic_media_play;
        Intent player = new Intent(this,ItemFragment.class);
        player.putExtra(EXTRA_SONG, s);
        player.putExtra(EXTRA_PLAYING, playing);

        b.setContentTitle(getString(R.string.title_activity_player))
                .setContentText(s.toString())
                .setSmallIcon(drawable)
                .setTicker(s.toString())
                .setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(this, 0, player, PendingIntent.FLAG_UPDATE_CURRENT));
        this.stopForeground(true);
        //el id no puede ser 0
        //Notification not = b.build();
        //not.contentIntent = PendingIntent.getActivity(this, 0, player, 0);
        this.startForeground(1, b.build());

        //Enviando a la Interfaz Gráfica
        Intent i = new Intent(PLAYING_INFO);
        i.putExtra(EXTRA_PLAYING, playing);
        i.putExtra(EXTRA_SONG, s);
        this.localBroadcastManager.sendBroadcast(i);
    }
}
