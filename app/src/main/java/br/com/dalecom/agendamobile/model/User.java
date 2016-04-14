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
import java.io.Serializable;
import java.util.List;

import br.com.dalecom.agendamobile.utils.LogUtils;

/**
 * Created by daniellessa on 25/03/16.
 */
@Table(name="User")
public class User extends Model implements Serializable {


    public long id;

    @Column(name = "IdServer")
    @Expose
    @SerializedName("id")
    public int idServer;

    @Column(name = "RegistrationId")
    @Expose
    @SerializedName("registration_id")
    public String registrationId;

    @Column(name = "Email")
    @Expose
    @SerializedName("email")
    public String email;

    @Column(name = "Password")
    @Expose
    @SerializedName("password")
    public String password;

    @Column(name = "Name")
    @Expose
    @SerializedName("name")
    public String name;

    @Column(name = "Sex")
    @Expose
    @SerializedName("sex")
    public String sex;

    @Column(name = "Role")
    @Expose
    @SerializedName("roles")
    public int role;

    @Column(name = "PhotoPath")
    @Expose
    @SerializedName("photo_path")
    public String photoPath;

    @Column(name = "BucketPath")
    @Expose
    @SerializedName("bucket_name")
    protected String bucketPath;

    @Column(name = "Professional")
    @Expose
    @SerializedName("professional")
    protected Professional professional;

    @Column(name = "LocalImageLocation")
    private String localImageLocation;

    private boolean imageSynced;



    public User() {
      super();
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String user) {
        this.name = user;
    }

    public static User getUserByServerId(long serverId) {
        User user = new Select()
                .from(User.class)
                .where("IdServer = ?", serverId)
                .executeSingle();

        return user;
    }

    public static User getUserById(long userId) {
        User user = new Select()
                .from(User.class)
                .where("Id = ?", userId)
                .executeSingle();

        return user;
    }

    public boolean isImageSynced() {
        return imageSynced;
    }

    public void setAsImageSynced() {
        imageSynced = true;
    }

    public void setAsNotSynced() {
        imageSynced = false;
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

    public String getLocalImageLocation() {
        return localImageLocation;
    }

    public void setLocalImageLocation(String localImageLocation) {
        this.localImageLocation = localImageLocation;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }


    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(int id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageSynced(boolean imageSynced) {
        this.imageSynced = imageSynced;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getBucketPath() {
        return bucketPath;
    }

    public void setBucketPath(String bucketPath) {
        this.bucketPath = bucketPath;
    }


    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

}
