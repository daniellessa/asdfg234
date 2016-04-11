package br.com.dalecom.agendamobile.service.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by daniellessa on 03/04/16.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        String fromRegistredId = data.getString("from_registred_id");

        Log.d(LogUtils.TAG, "FromUser: " + fromRegistredId);
        Log.d(LogUtils.TAG, "Message: " + message);

        gerarNotificacao(message,message,fromRegistredId);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
    }


    public void gerarNotificacao(CharSequence ticker,CharSequence titulo,CharSequence descricao){

        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(ticker);
        builder.setContentTitle(titulo);
        builder.setContentText(descricao);
        builder.setColor(Color.parseColor("#9C27B0"));
        builder.setSmallIcon(R.drawable.ic_icalendar_transparent);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_icalendar_transparent));


        //builder.setContentIntent(p);

        Notification n = builder.build();
        n.vibrate = new long[]{150,300,150,600};
        n.flags = Notification.FLAG_AUTO_CANCEL;

        nm.notify(R.drawable.ic_icalendar_transparent,n);


        try{
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(this,som);
            toque.play();
        }catch (Exception e){};
    }
}
