package br.com.dalecom.agendamobile.service.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by daniellessa on 05/05/16.
 */
public class UpstreamMessage extends AsyncTask<Void,String, String> {

    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId;
    private Context mContext;
    private String message;

    public UpstreamMessage (Context context, String message) {
        mContext = context;
        this.message = message;
        gcm = new GoogleCloudMessaging();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            Bundle data = new Bundle();
            data.putString("my_message", "Hello World");
            data.putString("my_action","SAY_HELLO");
            String id = Integer.toString(msgId.incrementAndGet());
            gcm.send(S.SENDER_ID + "@gcm.googleapis.com", id, data);
            msg = "Sent message";
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
