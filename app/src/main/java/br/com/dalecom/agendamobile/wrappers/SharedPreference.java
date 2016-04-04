package br.com.dalecom.agendamobile.wrappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;


/**
 * Created by viniciuslima on 11/23/15.
 */
public class SharedPreference {


    private final String CURRENT_LOCATION = "mLocation";

    private final String CURRENT_USER_PREF = "mCurrentUser";
    private final String CURRENT_USER_ID = "Id";
    private final String CURRENT_USER_SERVER_ID = "IdServer";
    private final String CURRENT_USER_EMAIL = "Email";
    private final String CURRENT_USER_BUCKET_PATH = "BucketPath";
    private final String CURRENT_USER_PHOTO_PATH = "PhotoPath";
    private final String CURRENT_USER_NAME = "Name";

    private final String CURRENT_USER_REGISTRATION_ID = "Token";
    private final String CURRENT_USER_TOKEN = "Token";

    private final String LOCATION_LATITUDE = "latitude";
    private final String LOCATION_LONGITUDE = "longitude";

    private final String CURRENT_PROPERTY_REF = "mCurrentProperty";
    private final String CURRENT_PROPERTY_ID = "idServer";
    private final String CURRENT_PROPERTY_PIN = "pin";
    private final String CURRENT_PROPERTY_NAME = "name";
    private final String CURRENT_PROPERTY_BUCKET_PATH = "bucketPath";
    private final String CURRENT_PROPERTY_PHOTO_PATH = "PhotoPath";
    private final String CURRENT_PROPERTY_INFO = "info";


    private SharedPreferences sharedPreferences = null;
    Context mContext;

    @Inject
    public SharedPreference(Context context) {
        mContext = context;
    }

    public void setCurrentUser(User user) {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        sharedPreferences.edit().putLong(CURRENT_USER_ID, user.getId()).commit();
        sharedPreferences.edit().putLong(CURRENT_USER_SERVER_ID, user.getIdServer()).commit();
        sharedPreferences.edit().putString(CURRENT_USER_EMAIL, user.getEmail()).commit();
        sharedPreferences.edit().putString(CURRENT_USER_BUCKET_PATH, user.getBucketPath()).commit();
        sharedPreferences.edit().putString(CURRENT_USER_PHOTO_PATH, user.getPhotoPath()).commit();
        sharedPreferences.edit().putString(CURRENT_USER_NAME, user.getName()).commit();
    }

    public User getCurrentUser() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        User user = new User();
        user.setId(sharedPreferences.getLong(CURRENT_USER_ID, 0));
        user.setIdServer(sharedPreferences.getLong(CURRENT_USER_SERVER_ID, 0));
        user.setName(sharedPreferences.getString(CURRENT_USER_NAME, ""));
        user.setEmail(sharedPreferences.getString(CURRENT_USER_EMAIL, ""));
        user.setBucketPath(sharedPreferences.getString(CURRENT_USER_BUCKET_PATH, ""));
        user.setPhotoPath(sharedPreferences.getString(CURRENT_USER_PHOTO_PATH, ""));
        return user;
    }

    public void setUserToken(String token) {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        sharedPreferences.edit().putString(CURRENT_USER_TOKEN,token).commit();
    }

    public String getUserToken() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        return sharedPreferences.getString(CURRENT_USER_TOKEN, null);
    }

    public boolean hasUserToken() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        if ( sharedPreferences.getString(CURRENT_USER_TOKEN,null) == null )
            return false;

        return true;
    }

    public void clearUserToken() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        sharedPreferences.edit().putString(CURRENT_USER_TOKEN, null).commit();
    }

    public void setUserRegistrationId(String token) {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        sharedPreferences.edit().putString(CURRENT_USER_REGISTRATION_ID,token).commit();
    }

    public String getUserRegistrationId() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        return sharedPreferences.getString(CURRENT_USER_REGISTRATION_ID, null);
    }

    public boolean hasUserRegistrationId() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        if ( sharedPreferences.getString(CURRENT_USER_REGISTRATION_ID,null) == null )
            return false;

        return true;
    }

    public void clearUserRegistrationId() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_USER_PREF, 0);
        sharedPreferences.edit().putString(CURRENT_USER_REGISTRATION_ID, null).commit();
    }


    public void setLastLocation(Location location) {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_LOCATION, 0);
        sharedPreferences.edit().putFloat(LOCATION_LATITUDE, (float) location.getLatitude()).commit();
        sharedPreferences.edit().putFloat(LOCATION_LONGITUDE, (float) location.getLongitude()).commit();
    }

    public Location getLastLocation() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_LOCATION, 0);
        Double latitude = Double.parseDouble(new Float(sharedPreferences.getFloat(LOCATION_LATITUDE, 0f)).toString());
        Double longitude = Double.parseDouble(new Float(sharedPreferences.getFloat(LOCATION_LONGITUDE, 0f)).toString());

        Location location = new Location("LastKnownLocation");
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        return location;
    }


    public void setCurrentProperty(Property property) {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_PROPERTY_REF, 0);
        sharedPreferences.edit().putInt(CURRENT_PROPERTY_ID, property.getIdServer()).commit();
        sharedPreferences.edit().putString(CURRENT_PROPERTY_PIN, property.getPin()).commit();
        sharedPreferences.edit().putString(CURRENT_PROPERTY_NAME, property.getName()).commit();
        sharedPreferences.edit().putString(CURRENT_PROPERTY_PHOTO_PATH, property.getPhoto_path()).commit();
        sharedPreferences.edit().putString(CURRENT_PROPERTY_BUCKET_PATH, property.getBucketPath()).commit();
        sharedPreferences.edit().putString(CURRENT_PROPERTY_INFO, property.getInfo()).commit();
    }

    public Property getCurrentProperty() {
        sharedPreferences = mContext.getSharedPreferences(CURRENT_PROPERTY_REF, 0);
        Property property = new Property();
        property.setIdServer(sharedPreferences.getInt(CURRENT_PROPERTY_ID, 0));
        property.setPin(sharedPreferences.getString(CURRENT_PROPERTY_PIN, ""));
        property.setName(sharedPreferences.getString(CURRENT_PROPERTY_NAME, ""));
        property.setPhoto_path(sharedPreferences.getString(CURRENT_PROPERTY_PHOTO_PATH, ""));
        property.setBucketPath(sharedPreferences.getString(CURRENT_PROPERTY_BUCKET_PATH, ""));
        property.setInfo(sharedPreferences.getString(CURRENT_PROPERTY_INFO, ""));
        return property;
    }
}
