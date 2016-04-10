package br.com.dalecom.agendamobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by daniellessa on 25/03/16.
 */
@Table(name = "Service")
public class Service extends Model {

    @Column(name = "idServe")
    protected int idServer;

    @Column(name = "Title")
    @Expose
    @SerializedName("name")
    protected String title;

    @Column(name = "Hours")
    @Expose
    @SerializedName("hours")
    protected int hours;

    @Column(name = "Minutes")
    @Expose
    @SerializedName("minutes")
    protected int minutes;

    @Column(name = "Price")
    @Expose
    @SerializedName("price")
    protected float price;

    @Column(name = "Info")
    @Expose
    @SerializedName("info")
    protected String info;



    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int id) {
        this.idServer = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
