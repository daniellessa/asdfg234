package br.com.dalecom.agendamobile.model;

import android.net.Uri;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

import br.com.dalecom.agendamobile.utils.LogUtils;

/**
 * Created by daniellessa on 27/03/16.
 */
@Table(name = "Property")
public class Property extends Model{

    protected long id;

    @Column(name = "IdServer")
    protected int idServer;

    @Column(name = "Pin")
    protected String pin;

    @Column(name = "Name")
    @Expose
    @SerializedName("name")
    protected String name;

    @Column(name = "Photo_path")
    @Expose
    @SerializedName("photo_path")
    protected String photo_path;

    @Column(name = "Bucket_name")
    @Expose
    @SerializedName("bucket_name")
    protected String bucketPath;

    @Column(name = "Info")
    @Expose
    @SerializedName("info")
    protected String info;

    @Column(name = "LocalImageLocation")
    protected String localImageLocation;

    @Column(name = "OpenDay")
    @Expose
    @SerializedName("open_day")
    protected String openDay;

    @Column(name = "OpenHour")
    @Expose
    @SerializedName("open_hour")
    protected String openHour;

    @Column(name = "Phone")
    @Expose
    @SerializedName("phone")
    protected String phone;

    @Column(name = "Street")
    @Expose
    @SerializedName("street")
    protected String street;

    @Column(name = "Number")
    @Expose
    @SerializedName("number")
    protected String number;

    @Column(name = "City")
    @Expose
    @SerializedName("city")
    protected String city;

    @Column(name = "Lat")
    @Expose
    @SerializedName("lat")
    protected float lat;

    @Column(name = "Lng")
    @Expose
    @SerializedName("lng")
    protected float lng;

    @Column(name = "Notification")
    @Expose
    protected boolean notification;



    public void setId(long id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setId(int id) {
        this.id = (long) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public String getBucketPath() {
        return bucketPath;
    }

    public void setBucketPath(String bucketPath) {
        this.bucketPath = bucketPath;
    }

    public String getOpenDay() {
        return openDay;
    }

    public void setOpenDay(String openDay) {
        this.openDay = openDay;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public static List<Property> getAll(){
        return new Select()
                .from(Property.class)
                .orderBy("name ASC")
                .execute();
    }

    public static Property findOne(int idServer){
        return new Select()
                .from(Property.class)
                .where("IdServer = ?", idServer)
                .executeSingle();
    }

    public String getLocalImageLocation() {
        return localImageLocation;
    }

    public void setLocalImageLocation(String localImageLocation) {
        this.localImageLocation = localImageLocation;
    }

    public void setLocalImageLocationAndDeletePreviousIfExist(String photoUri) {

        if ( this.localImageLocation != null && this.localImageLocation !=  photoUri)
        {
            Log.d(LogUtils.TAG, "replace image location: " + this.localImageLocation);
            Uri uri = Uri.parse(this.localImageLocation);
            File f = new File( uri.getPath() );
            if ( f.exists() )
            {
                Log.d(LogUtils.TAG,"file exists: " + this.localImageLocation);
                if ( f.delete() )
                    Log.d(LogUtils.TAG,"file deleted: " + this.localImageLocation);
            }
        }

        this.localImageLocation = photoUri;
    }

}
