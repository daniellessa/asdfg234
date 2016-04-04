package br.com.dalecom.agendamobile.wrappers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import br.com.dalecom.agendamobile.utils.LogUtils;


/**
 * Created by viniciuslima on 12/7/15.
 */
public class LocationAcquirer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    protected Context mContext;
    private Location mLastLocation;
    private LocationAcquirerCallBack locationAcquirerCallBack;
    private boolean isConnected = false;

    public LocationAcquirer(Context context) {
        this.mContext = context;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public LocationAcquirer(Context context,LocationAcquirerCallBack locationAcquirerCallBack) {
        this.mContext = context;
        this.locationAcquirerCallBack = locationAcquirerCallBack;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public void getLastKnownLocation() {
        if ( !isConnected )
        {
            mGoogleApiClient.connect();
        }
        else
        {
            if ( mLastLocation != null )
                this.locationAcquirerCallBack.onSuccess(mLastLocation);
            else
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                this.locationAcquirerCallBack.onSuccess(mLastLocation);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        isConnected = true;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            Log.d(LogUtils.TAG,"mLastLocation == null");
            locationAcquirerCallBack.onError();
        } else {
            if ( locationAcquirerCallBack != null )
                locationAcquirerCallBack.onSuccess(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isConnected = false;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public interface LocationAcquirerCallBack {
        void onSuccess(Location location);
        void onError();
    }
}
