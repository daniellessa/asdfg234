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

import com.google.gson.annotations.Expose;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Alert;
import br.com.dalecom.agendamobile.ui.AlertsActivity;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by daniellessa on 03/04/16.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private int idServer;
    private int type;
    private String date;
    private String time;
    private String title;
    private String message;
    private int propertyId;
    private String fromRegistredId;
    private Intent intent;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(LogUtils.TAG, "Data: " + data.toString());

        idServer = Integer.valueOf(data.getString("id"));
        type = Integer.valueOf(data.getString("type"));
        date = data.getString("date");
        time = data.getString("time");
        title = data.getString("title");
        message = data.getString("message");
        fromRegistredId = data.getString("from_registred_id");
        try {
            propertyId = Integer.valueOf(data.getString("property_id"));
        }catch (NumberFormatException ex){
            propertyId = 0;
        }


        intent = null;

        switch (type){
            case 1:
                intent = new Intent(getApplicationContext(), AlertsActivity.class);
                saveAlert();
                gerarNotificacao("Teste",title,message);
                break;
            case 2:
                intent = new Intent(getApplicationContext(), AlertsActivity.class);
                saveAlert();
                gerarNotificacao("Teste", title, message);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), AlertsActivity.class);
                saveAlert();
                gerarNotificacao("Teste", title, message);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), AlertsActivity.class);
                saveAlert();
                gerarNotificacao("Teste", title, message);
                break;
            case 5:
                Log.d(LogUtils.TAG, "type: " + type);
                break;
            default:
                Log.d(LogUtils.TAG, "type: " + type);
                break;
        }


//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }
    }

    private void saveAlert(){
        Alert alert = new Alert(idServer, title, message, fromRegistredId, DateHelper.toString(date), DateHelper.hourToString(time), type);
        if(Alert.findOne(idServer) == null)
            alert.save();

        Log.d(LogUtils.TAG, "IdServer Alert: "+ alert.getIdServer());
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

        if(intent != null){
            PendingIntent p = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            builder.setContentIntent(p);
        }


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
