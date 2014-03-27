package com.fewstera.injectablemedicinesguide;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.string.InFileStringObjectPersister;
import com.octo.android.robospice.request.notifier.RequestListenerNotifier;

/**
 * Created by fewstera on 23/03/2014.
 */
public class DownloadService  extends UncachedSpiceService {
    @Override
    public int getThreadCount() {
        return 3;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v("SampleSpiceService", "Starting service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("SampleSpiceService","Starting service - new");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("SampleSpiceService","Stopping service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("SampleSpiceService","Bound service");
        return super.onBind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v("SampleSpiceService","Rebound service");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("SampleSpiceService","Unbound service");
        return super.onUnbind(intent);
    }

    @Override
    protected RequestListenerNotifier createRequestRequestListenerNotifier() {
        return new DownloadRequestListenerNotifier();
    }

    // Suppressing deprecation, as deprecated method have to be used for older device notifications
    @SuppressWarnings("deprecation")
    @Override
    public Notification createDefaultNotification() {
        String title = getResources().getString(R.string.app_name);
        String subTitle = getResources().getString(R.string.service_message);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this).setContentTitle(title)
                    .setContentText(subTitle).setSmallIcon(getApplicationInfo().icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).build();
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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