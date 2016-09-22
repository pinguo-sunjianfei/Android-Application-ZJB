package com.idrv.coach.utils.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.idrv.coach.R;


/**
 * time: 15/7/27
 * description: notification相关的通知
 *
 * @author sunjianfei
 */
public class NotifyHelper {
    public static void notifyProgress(Context context, float progress, float total) {
        String formatProgress = ResHelper.getString(R.string.download_progress);
        if (total > 0) {
            formatProgress = String.format(formatProgress, Math.round(progress / total * 100) + "%");
        } else {
            formatProgress = ResHelper.getString(R.string.download_finish);
        }
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentText(formatProgress)
                .setContentTitle(ResHelper.getString(R.string.main_version_update))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_app))
                .setSmallIcon(R.mipmap.notify_small_icon)
                .setProgress((int) total, (int) progress, false)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }

}
