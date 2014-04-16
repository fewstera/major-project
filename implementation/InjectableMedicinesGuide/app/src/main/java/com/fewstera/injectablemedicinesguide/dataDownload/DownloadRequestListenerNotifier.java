package com.fewstera.injectablemedicinesguide.dataDownload;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.octo.android.robospice.request.notifier.RequestListenerNotifier;

import java.util.Set;

import roboguice.util.temp.Ln;

/**
 * Default implementation of RequestListenerNotifier. It will notify listeners
 * on the ui thread.
 *
 * I had to copy all the code from the robospice DefaultRequestListenerNotifier class, as the parameters
 * within that class are private, so inheritance could not be used to modify the behaviour of the class.
 *
 * All that I have added is that when Request finishes the results are added to the DataProgress singleton.
 *
 * Original source:
 * https://github.com/stephanenicolas/robospice/blob/release/robospice-core-parent/robospice/src/main/java/com/octo/android/robospice/request/notifier/DefaultRequestListenerNotifier.java
 *
 * @author Andrew Clark, Aidan Wynne Fewster
 * @version 1.1
 * @since 1.0
 * @see com.octo.android.robospice.request.notifier.DefaultRequestListenerNotifier
 */
public class DownloadRequestListenerNotifier implements RequestListenerNotifier {
    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================
    private final Handler handlerResponse;
    private final DataProgress _dataProgress;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public DownloadRequestListenerNotifier() {
        handlerResponse = new Handler(Looper.getMainLooper());
        _dataProgress = DataProgress.getInstance();
    }

    private void post(final Runnable r, final Object token) {
        handlerResponse.postAtTime(r, token, SystemClock.uptimeMillis());
    }

    public <T> void notifyListenersOfRequestNotFound(final CachedSpiceRequest<T> request, final Set<RequestListener<?>> listRequestListener) {
        post(new NotFoundRunnable(listRequestListener), request.getRequestCacheKey());
    }

    public <T> void notifyListenersOfRequestAdded(final CachedSpiceRequest<T> request, Set<RequestListener<?>> listeners) {
        // does nothing for now
    }

    public <T> void notifyListenersOfRequestAggregated(final CachedSpiceRequest<T> request, Set<RequestListener<?>> listeners) {
        // does nothing for now
    }

    public <T> void notifyListenersOfRequestProgress(final CachedSpiceRequest<T> request, final Set<RequestListener<?>> listeners, final RequestProgress progress) {

        post(new ProgressRunnable(listeners, progress), request.getRequestCacheKey());
    }

    public <T> void notifyListenersOfRequestSuccess(final CachedSpiceRequest<T> request, final T result, final Set<RequestListener<?>> listeners) {
        /* Increase finish request count */
        _dataProgress.increaseFinishedCount();
        post(new ResultRunnable<T>(listeners, result), request.getRequestCacheKey());
    }

    public <T> void notifyListenersOfRequestFailure(final CachedSpiceRequest<T> request, final SpiceException e, final Set<RequestListener<?>> listeners) {
        /* Increase finish request count */
        _dataProgress.increaseFinishedCount();
        post(new ResultRunnable<T>(listeners, e), request.getRequestCacheKey());
    }

    public <T> void notifyListenersOfRequestCancellation(final CachedSpiceRequest<T> request, final Set<RequestListener<?>> listeners) {

        post(new ResultRunnable<T>(listeners, new RequestCancelledException("Request has been cancelled explicitely.")), request.getRequestCacheKey());
    }

    public <T> void clearNotificationsForRequest(final CachedSpiceRequest<T> request, final Set<RequestListener<?>> listeners) {

        handlerResponse.removeCallbacksAndMessages(request.getRequestCacheKey());
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    private static class NotFoundRunnable implements Runnable {
        private final Set<RequestListener<?>> listeners;

        public NotFoundRunnable(final Set<RequestListener<?>> listeners) {
            this.listeners = listeners;
        }

        public void run() {

            if (listeners == null) {
                return;
            }

            Ln.v("Notifying " + listeners.size() + " listeners of request not found");
            synchronized (listeners) {
                for (final RequestListener<?> listener : listeners) {
                    if (listener != null && listener instanceof PendingRequestListener) {
                        Ln.v("Notifying %s", listener.getClass().getSimpleName());
                        ((PendingRequestListener<?>) listener).onRequestNotFound();
                    }
                }
            }
        }
    }

    private static class ProgressRunnable implements Runnable {
        private final RequestProgress progress;
        private final Set<RequestListener<?>> listeners;

        public ProgressRunnable(final Set<RequestListener<?>> listeners, final RequestProgress progress) {
            this.progress = progress;
            this.listeners = listeners;
        }

        public void run() {

            if (listeners == null) {
                return;
            }

            Ln.v("Notifying " + listeners.size() + " listeners of progress " + progress);
            synchronized (listeners) {
                for (final RequestListener<?> listener : listeners) {
                    if (listener != null && listener instanceof RequestProgressListener) {
                        Ln.v("Notifying %s", listener.getClass().getSimpleName());
                        ((RequestProgressListener) listener).onRequestProgressUpdate(progress);
                    }
                }
            }
        }
    }

    private static class ResultRunnable<T> implements Runnable {

        private SpiceException spiceException;
        private T result;
        private final Set<RequestListener<?>> listeners;

        public ResultRunnable(final Set<RequestListener<?>> listeners, final T result) {
            this.result = result;
            this.listeners = listeners;
        }

        public ResultRunnable(final Set<RequestListener<?>> listeners, final SpiceException spiceException) {
            this.spiceException = spiceException;
            this.listeners = listeners;
        }

        public void run() {
            if (listeners == null) {
                return;
            }

            final String resultMsg = spiceException == null ? "success" : "failure";
            Ln.v("Notifying " + listeners.size() + " listeners of request " + resultMsg);
            synchronized (listeners) {
                for (final RequestListener<?> listener : listeners) {
                    if (listener != null) {
                        @SuppressWarnings("unchecked")
                        final RequestListener<T> listenerOfT = (RequestListener<T>) listener;
                        Ln.v("Notifying %s", listener.getClass().getSimpleName());
                        if (spiceException == null) {
                            listenerOfT.onRequestSuccess(result);
                        } else {
                            listener.onRequestFailure(spiceException);
                        }
                    }
                }
            }
        }
    }
}
