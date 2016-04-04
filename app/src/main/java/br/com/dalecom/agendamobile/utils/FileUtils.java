package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;


/**
 * Created by viniciuslima on 8/13/15.
 */
public class FileUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    @Inject
    public SharedPreference sharedPreference;

    @Inject
    public FileUtils(Context context) {
        ((AgendaMobileApplication) context).getAppComponent().inject(this);
    }

    public String getFullBucketPath() {
        return S.BUCKET_PROPERTIES_DATABASE_NAME +"/"+ getCurrentUserBucketPath();
    }

    public String getUniqueName() {
        // TODO
        Long tsLong = System.currentTimeMillis();

        return S.FILE_PREFIX + Long.toString( tsLong );
    }

    public String getUniqueName(Integer increment) {
        // TODO
        Long tsLong = System.currentTimeMillis();

        return S.FILE_PREFIX + Long.toString( tsLong ) + "_" + increment;
    }

    public String getUniqueName(String increment) {
        // TODO
        Long tsLong = System.currentTimeMillis();

        return S.FILE_PREFIX + Long.toString( tsLong ) + "_" + increment;
    }

    /** Create a File for saving an image or video */
    public File getOutputMediaFile(int type,String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // TODO - using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "iCalendar");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d(LogUtils.TAG, "failed to create directory -> iCalendar");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + S.FILE_PREFIX + fileName +  S.JPG_EXT);
        }
        else
        {
            return null;
        }

        return mediaFile;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromByteArray(byte[] currentImageBytes, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes.length, options);
    }

    public boolean deleteFileByURI(URI uri) {
        File file = new File(uri);
        return file.delete();
    }

    public boolean deleteFileByUriString(String uri) {
        File file = new File(uri);
        return file.delete();
    }

    public String getCurrentBucketName() {
        return S.BUCKET_PROPERTIES_DATABASE_NAME;
    }

    public String getCurrentUserBucketPath() {
        return S.BUCKET_PROFILE_DATABASE_NAME;
    }

    public String getCurrentProfileBucketName() {
        return S.BUCKET_PROFILE_DATABASE_NAME;
    }

    public String getCurrentResizedProfileBucketName() {
        return S.BUCKET_RESIZED_PROFILE_DATABASE_NAME;
    }
}
