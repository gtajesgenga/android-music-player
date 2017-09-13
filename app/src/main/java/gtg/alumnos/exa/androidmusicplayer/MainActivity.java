package gtg.alumnos.exa.androidmusicplayer;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ArtistsFragment.OnListFragmenArtistInteractionListener,
        AlbumsFragment.OnListFragmentAlbumInteractionListener,
        SongsFragment.OnListFragmentSongInteractionListener,
        ItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_artist) {
            fragment = new ArtistsFragment();
        } else if (id == R.id.nav_album) {
            fragment = AlbumsFragment.newInstance(1, null, null);
        } else if (id == R.id.nav_song) {
            fragment = SongsFragment.newInstance(1, null, null);
        }
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentArtistInteraction(Artist item) {
        StringBuffer selection = new StringBuffer(MediaStore.Audio.AlbumColumns.ARTIST);
        selection.append("=?");
        AlbumsFragment fragment = AlbumsFragment.newInstance(1, selection.toString(), new String[]{item.getName()});
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onOverflowArtistInteraction(Artist item) {
        StringBuffer selection = new StringBuffer();
        StringBuffer selectionArgs = new StringBuffer();
        if (item.getName() != null) {
            selection.append(",").append(MediaStore.Audio.Media.ARTIST);
            selectionArgs.append(",").append(item.getName());
        }

        if (selection.toString().startsWith(","))
            selection.replace(0, selection.length(), selection.toString().replaceFirst(",", ""));

        if (selectionArgs.toString().startsWith(","))
            selectionArgs.replace(0, selectionArgs.length(), selectionArgs.toString().replaceFirst(",", ""));

        ItemFragment fragment = ItemFragment.newInstance(1, selection.toString(), selectionArgs.toString().split(","));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onListFragmentAlbumInteraction(Album item) {
        StringBuffer selection = new StringBuffer(MediaStore.Audio.Media.ALBUM_ID);
        selection.append("=?");
        SongsFragment fragment = SongsFragment.newInstance(1, selection.toString(), new String[]{item.getId().toString()});
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onOverflowAlbumInteraction(Album item) {
        StringBuffer selection = new StringBuffer();
        StringBuffer selectionArgs = new StringBuffer();
//        if (item.getAlbum() != null) {
//            selection.append(",").append(MediaStore.Audio.Media.ALBUM);
//            selectionArgs.append(",").append(item.getAlbum());
//        }

        if (item.getId() != null) {
            selection.append(",").append(MediaStore.Audio.Media.ALBUM_ID);
            selectionArgs.append(",").append(item.getId().toString());
        }

//        if (item.getArtist() != null) {
//            selection.append(",").append(MediaStore.Audio.Media.ARTIST);
//            selectionArgs.append(",").append(item.getArtist());
//        }

        if (item.getAlbum_art() != null) {
            selection.append(",").append(MediaStore.Audio.AlbumColumns.ALBUM_ART);
            selectionArgs.append(",").append(item.getAlbum_art());
        }

        if (selection.toString().startsWith(","))
            selection.replace(0, selection.length(), selection.toString().replaceFirst(",", ""));

        if (selectionArgs.toString().startsWith(","))
            selectionArgs.replace(0, selectionArgs.length(), selectionArgs.toString().replaceFirst(",", ""));

        ItemFragment fragment = ItemFragment.newInstance(1, selection.toString(), selectionArgs.toString().split(","));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onListFragmentSongInteraction(Song item) {
        StringBuffer selection = new StringBuffer();
        StringBuffer selectionArgs = new StringBuffer();
        if (item.getAlbum_id() != null) {
            selection.append(",").append(MediaStore.Audio.Media.ALBUM_ID);
            selectionArgs.append(",").append(item.getAlbum_id().toString());
        }

        if (item.getArtist() != null) {
            selection.append(",").append(MediaStore.Audio.Media.ARTIST);
            selectionArgs.append(",").append(item.getArtist());
        }

        if (item.getAlbumArt() != null) {
            selection.append(",").append(MediaStore.Audio.AlbumColumns.ALBUM_ART);
            selectionArgs.append(",").append(item.getAlbumArt());
        }

        if (item.getUri() != null) {
            selection.append(",").append(MediaStore.Audio.Media.DATA);
            selectionArgs.append(",").append(item.getUri());
        }

        if (selection.toString().startsWith(","))
            selection.replace(0, selection.length(),selection.toString().replaceFirst(",", ""));

        if (selectionArgs.toString().startsWith(","))
            selectionArgs.replace(0, selectionArgs.length(),selectionArgs.toString().replaceFirst(",", ""));

        ItemFragment fragment = ItemFragment.newInstance(1, selection.toString(), selectionArgs.toString().split(","));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onListFragmentInteraction(Song item) {

    }
}
