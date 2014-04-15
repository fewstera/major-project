package com.fewstera.injectablemedicinesguide.dataDownload;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.fewstera.injectablemedicinesguide.R;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.request.notifier.RequestListenerNotifier;

/**
 * RobospiceService class for handling the download of data. This is an uncached service
 * as the data is downloaded infrequently, so the cache will most likely have expired by the
 * next time the request is made.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 * @see com.octo.android.robospice.SpiceService
 */
public class DownloadService  extends UncachedSpiceService {
    /**
     * Get the max amount of threads
     *
     * @return the max amount of threads
     */
    @Override
    public int getThreadCount() {
        return 3;
    }

    /**
     * Gets the request listener notifier
     *
     * @return the request listener notifier
     */
    @Override
    protected RequestListenerNotifier createRequestRequestListenerNotifier() {
        return new DownloadRequestListenerNotifier();
    }

    /**
     * Returns the Notification to be used whilst the Service is running. This allows for a notification
     * to be displayed within the notification center whilst download is in progress.
     *
     * Suppressing deprecation, as deprecated method have to be used for older device notifications
     *
     * @return the notification to display
     *
     * This code if very similar to the code within the SpiceService class as found here
     * https://github.com/stephanenicolas/robospice/blob/release/robospice-core-parent/robospice/src/main/java/com/octo/android/robospice/SpiceService.java
     */
    @SuppressWarnings("deprecation")
    @Override
    public Notification createDefaultNotification() {
        String title = getResources().getString(R.string.app_name);
        String subTitle = getResources().getString(R.string.service_message);

        Notification notification = null;
        /* If build version is greater than JellyBean */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this).setContentTitle(title)
                    .setContentText(subTitle).setSmallIcon(getApplicationInfo().icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).build();
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            /* If build version is greater than HoneyComb */
            notification = new Notification.Builder(this).setSmallIcon(getApplicationInfo().icon)
                    .setContentTitle(title).setContentText(subTitle)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).getNotification();
        } else {
            notification = new Notification();
            notification.icon = getApplicationInfo().icon;
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
            notification.setLatestEventInfo(this, title, subTitle, pendingIntent);
            notification.tickerText = null;
            notification.when = System.currentTimeMillis();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.priority = Notification.PRIORITY_MIN;
        }

        return notification;
    }
}