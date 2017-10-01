package gtg.alumnos.exa.androidmusicplayer.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import gtg.alumnos.exa.androidmusicplayer.R;
import gtg.alumnos.exa.androidmusicplayer.activities.MainActivity;
import gtg.alumnos.exa.androidmusicplayer.services.MediaPlayerService;

/**
 * Implementation of App Widget functionality.
 */
public class PlayerWidget extends AppWidgetProvider {

    private MediaSessionCompat.Token token;
    private MediaControllerCompat mediacontroller;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.player_widget);
        // Instruct the widget manager to update the widget
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.INIT_PLAYER, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_PREVIOUS);
        pendingIntent = PendingIntent.getService(context, 3, intent, 0);
        views.setOnClickPendingIntent(R.id.btn_prev, pendingIntent);

        intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_NEXT);
        pendingIntent = PendingIntent.getService(context, 2, intent, 0);
        views.setOnClickPendingIntent(R.id.btn_next, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals("TOKEN")) {
            token = intent.getParcelableExtra("tkn");
            try {
                mediacontroller = new MediaControllerCompat(context, token);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}

