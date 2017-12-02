package gtg.alumnos.exa.androidmusicplayer.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

/**
 * Created by gus on 23/09/17.
 */

public class StorageUtilLoader extends AsyncTaskLoader<StorageUtil> {

    CancellationSignal mCancellationSignal;
    private StorageUtil mCursor;

    public StorageUtilLoader(Context context) {
        super(context);
    }

    @Override
    public StorageUtil loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            StorageUtil storageUtil = new StorageUtil(getContext());
            return storageUtil;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(StorageUtil storageUtil) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (storageUtil != null) {
                storageUtil = null;
            }
            return;
        }
        StorageUtil oldCursor = mCursor;
        mCursor = storageUtil;

        if (isStarted()) {
            super.deliverResult(storageUtil);
        }

        if (oldCursor != null && oldCursor != storageUtil) {
            oldCursor = null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(StorageUtil storageUtil) {
        if (storageUtil != null) {
            storageUtil = null;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null) {
            mCursor = null;
        }
        mCursor = null;
    }
}
