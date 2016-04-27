package co.noagames.plugin.localnotification;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

//import com.unity3d.player.UnityPlayerNativeActivity;

public class NotificationManager extends BroadcastReceiver
{

    public static void SetNotification(Context context,int id, long delayMs, String title, String message, String ticker, int sound, int vibrate,
                                       int lights, String smallIconResource, int bgColor, int executeMode)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("s_icon", smallIconResource);
        Log.i("localNotif", "timed notification "+ id+" set for :" + delayMs+"ms");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (executeMode == 2)
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT));
            else if (executeMode == 1)
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT));
            else
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        }
        else
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static void SetRepeatingNotification(Context context,int id, long delay, String title, String message, String ticker, long rep, int sound, int vibrate, int lights,
                                                String smallIconResource, int bgColor, String unityClass)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("s_icon", smallIconResource);
        am.setRepeating(0, System.currentTimeMillis() + delay, rep, PendingIntent.getBroadcast(context, id, intent, 0));
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("localNotif", "notification recived");
        String ticker = intent.getStringExtra("ticker");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String s_icon = intent.getStringExtra("s_icon");
        int color = intent.getIntExtra("color", 0);
        Boolean sound = intent.getBooleanExtra("sound", false);
        Boolean vibrate = intent.getBooleanExtra("vibrate", false);
        Boolean lights = intent.getBooleanExtra("lights", false);
        int id = intent.getIntExtra("id", 0);

        Resources res = context.getResources();

        Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(color);

        if (ticker != null && ticker.length() > 0)
            builder.setTicker(ticker);

        if (s_icon != null && s_icon.length() > 0)
            builder.setSmallIcon(res.getIdentifier(s_icon, "drawable", context.getPackageName()));
        else
            builder.setSmallIcon(R.drawable.notify_icon_small);

        if (sound)
            builder.setSound(RingtoneManager.getDefaultUri(2));

        if (vibrate)
            builder.setVibrate(new long[]{
                    1000L, 1000L
            });

        if (lights)
            builder.setLights(Color.GREEN, 3000, 3000);
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public static void CancelNotification(Context context,int id)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        am.cancel(pendingIntent);
    }

    public static void CancelAll(Context context){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }
}