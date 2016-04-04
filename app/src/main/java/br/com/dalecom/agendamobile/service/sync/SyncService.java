package br.com.dalecom.agendamobile.service.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import br.com.dalecom.agendamobile.utils.LogUtils;


/**
 * Created by viniciuslima on 11/10/15.
 *
 * Bound Service.
 * A component that allows the sync adapter framework to run the code in your sync adapter class.
 */
public class SyncService extends Service {

    private static SyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        Log.d(LogUtils.TAG, "SyncService onCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LogUtils.TAG, "SyncService onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
