package br.com.dalecom.agendamobile.service.sync.auth_stub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import br.com.dalecom.agendamobile.utils.LogUtils;


/**
 * Created by viniciuslima on 11/10/15.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        Log.d(LogUtils.TAG, "AuthenticatorService onCreate");
        mAuthenticator = new Authenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LogUtils.TAG, "AuthenticatorService onBind");
        return mAuthenticator.getIBinder();
    }
}
