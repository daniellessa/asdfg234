package br.com.dalecom.agendamobile.utils;

import android.content.Context;

import br.com.dalecom.agendamobile.R;

/**
 * Created by daniellessa on 24/03/16.
 */
public class S {


    public static final String AuthGoogle =  "661341416508-jmqeigejivot5erf7vhhukbofbphvsi0.apps.googleusercontent.com";
    public  static final String END_POINT_URL = "http://172.22.17.248.xip.io:1337";
    public  static final String FILE_PREFIX = "image_";
    public static final String JPG_EXT = ".jpg";
    public static final String BUCKET_PREFIX = "agendamobile";
    public static final String BUCKET_PROPERTIES_DATABASE_NAME = "agendamobile-properties-images";
    public static final String BUCKET_PROFILE_DATABASE_NAME = "agendamobile-profile-images";
    public static final String BUCKET_RESIZED_PROFILE_DATABASE_NAME = "resized-agendamobile-profile-images";
    public static final String ACCOUNT = "dummy_account";
    public static final String KEY_TOKEN = "token";
    public static final String COGNITO_POOL_ID = "us-east-1:8d371cd0-c2d7-491e-885d-d4f591b5ee76";
    public static final long SYNC_POLL_FREQUENCY = 2 * 60;
    public static String MixPanelProjectToken = "6cd5dce861e7931f425573d8e1285b9f";
    public static final int si = 2500;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SERVING = "serving";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_CANCELED = "canceled";
    public static final String STATUS_LATE = "late";

    //GMS PushMessage
    public static final String SERVER_API_KEY = "AIzaSyAWd9G1zvZ4WBuMe0_LvnSE0hqQyQer9ds";
    public static final String SENDER_ID = "987141080143";

    public static String getAuthority(Context context) {
        return context.getString(R.string.authority);
    }
}
