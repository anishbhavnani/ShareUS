package com.share.in.main.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask
        extends AsyncTask<String, Void, Bitmap> {

    Listener listener;
    public static interface Listener {
        void onImageDownloaded(final Bitmap bitmap);
        void onImageDownloadError();
    }

    public DownloadImageTask(final Listener listener) {
        this.listener = listener;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        final String url = urls[0];
        Bitmap bitmap = null;

        try {
            final InputStream inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final MalformedURLException malformedUrlException) {
            // Handle error
        } catch (final IOException ioException) {
            // Handle error
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap downloadedBitmap) {
        if (null != downloadedBitmap) {
            listener.onImageDownloaded(downloadedBitmap);
        } else {
            listener.onImageDownloadError();
        }
    }
}
