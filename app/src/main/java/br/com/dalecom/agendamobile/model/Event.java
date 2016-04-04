package br.com.dalecom.agendamobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by daniellessa on 25/03/16.
 */

@Table(name = "Event")
public class Event extends Model {

    public static final String STATUS_AGENDADO = "agendado";
    public static final String STATUS_ATRASADO = "atrasado";
    public static final String STATUS_ATENDIDO = "atendido";



    @Column(name = "IdServer")
    protected long idServer;

    @Column(name = "Professional")
    @Expose
    @SerializedName("professional")
    private User professional;

    @Column(name = "User")
    @Expose
    @SerializedName("user")
    private User user;

    @Column(name = "Service")
    @Expose
    @SerializedName("service")
    private Service service;

    @Column(name = "StartAt")
    @Expose
    @SerializedName("startAt")
    private String startAt;

    @Column(name = "EndsAt")
    @Expose
    @SerializedName("endsAt")
    private String endsAt;

    @Column(name = "status")
    @Expose
    @SerializedName("status")
    private String status;

    @Column(name = "Finalized")
    @Expose
    @SerializedName("finalazed")
    private boolean finalized;

    @Column(name = "FinalizedAt")
    @Expose
    @SerializedName("finalizedAt")
    private String finalizedAt;

    @Column(name="Token")
    @Expose
    @SerializedName("token")
    protected String token;


    public int getIdServer() {
        return (int)idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = (long) idServer;
    }

    public User getProfessional() {
        return professional;
    }

    public void setProfessional(User professional) {
        this.professional = professional;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public String getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(String finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
