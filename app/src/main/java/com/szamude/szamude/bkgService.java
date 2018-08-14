package com.szamude.szamude;

// notification service

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class bkgService extends Service {
    public bkgService() {
    }
    public Set<String> getTemp() {
        final SharedPreferences prefs = getSharedPreferences("szamude-trains", MODE_PRIVATE);
        Set<String> abos = prefs.getStringSet("temp",null);
        if(abos != null) {
            return abos;
        }
        return new HashSet<String>();
    }
    public List<String> tmp;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tmp = new ArrayList<String>(getTemp());
        usingCountDownTimer();

        return super.onStartCommand(intent, flags, startId);

    }




    public void sendNotification(String train, String desc) {
        //Get an instance of NotificationManager//
   /*     Intent intent = new Intent(this,MainAct2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,"trains")
                        .setSmallIcon(R.drawable.ic_train_black_24dp)
                        .setContentTitle(train)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc.toString()))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setChannel()
                .setContentText(desc);
*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "sztr";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "SZamude (zamude vlakov)", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("obvestila o zamudah vlakov");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 300, 300, 300});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_train_black_24dp)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(train)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(desc))
                .setContentInfo("Zamuda")
                .setContentText(desc);
        Random r = new Random();
        int i1 = r.nextInt(80 - 1) + 1;
        notificationManager.notify(/*notification id*/i1, notificationBuilder.build());

        //   NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//


    }

    CountDownTimer countDownTimer;

    public void usingCountDownTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 20000) {

            public void onTick(long millisUntilFinished) {
                // MainAct2 a = new MainAct2();
                // sendNotification("1642", "The delay of the train 1642 (Pragersko - Murska Sobota) on station Lipovci - 5 min DEPARTING");
                // sendNotification("asd1642","The delay of the train 16adsdadsadsda42 (Pragersko - Murska Sobota) on station Lipovci - 5 min DEPARTING");
                refresh();

            }

            public void onFinish() {
                start();
            }
        }.start();
    }

    Set<String> getAbos() {
        final SharedPreferences prefs = getSharedPreferences("szamude-trains", MODE_PRIVATE);
        Set<String> abos = prefs.getStringSet("abos", null);
        if (abos != null) {
            return abos;
        }
        return new HashSet<String>();
    }


    public void refresh() {

        Parser parser = new Parser();


        parser.execute("http://91.209.49.136/Web/RSS.aspx?lang=en");
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> list) {
                List<String> abos = new ArrayList<String>(getAbos());
                Log.i("ababab",""+list);
                for(int x = 0; x < tmp.size();x++) {
                    if(tc(tmp.get(x),list)) {
                        // delay gone
                        Log.i("Delay Gone",""+tmp.get(x));
                        tmp.remove(x);
                    }
                }
                for (int x = 0; x < list.size(); x++) {
                    for (int y = 0; y < abos.size(); y++) {
                        if ((list.get(x).getDescription().contains("train " + abos.get(y) + " "))) {
                            if(!tmp.contains(abos.get(y))) {
                                sendNotification(abos.get(y), list.get(x).getDescription());
                                tmp.add(abos.get(y));
                                Log.i("bbbbb",(""+tmp));
                            }
                        }
                    }

                }
                saveTmp();
            }

            @Override
            public void onError() {
                Log.i("RSS_ERR","error");
            }
            public boolean tc(String ab, ArrayList<Article> t) {
                for(int x = 0; x < t.size(); x++) {
                    if((t.get(x).getDescription().contains("train " + ab + " "))) return false;
                }
                return true;
            }
            public void saveTmp() {
                SharedPreferences.Editor edit = getSharedPreferences("szamude-trains",MODE_PRIVATE).edit();
                edit.putStringSet("temp",new HashSet<String>(tmp));
                edit.apply();
            }
        });
    }
}