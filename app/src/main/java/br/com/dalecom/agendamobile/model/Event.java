package br.com.dalecom.agendamobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by daniellessa on 25/03/16.
 */

@Table(name = "Event")
public class Event extends Model implements Serializable {


    @Column(name = "IdServer")
    protected long idServer;

    @Column(name = "UserProf")
    private User userProf;

    @Column(name = "ProfessionalId")
    @Expose
    @SerializedName("professionals_id")
    protected int professionalsId;

    @Column(name = "User_id")
    @Expose
    @SerializedName("users_id")
    protected int userId;

    private User user;

    @Column(name = "ServiceId")
    @Expose
    @SerializedName("services_id")
    protected int servicesId;

    @Column(name = "Day")
    @Expose
    @SerializedName("day")
    protected String day;

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
    @SerializedName("finalized")
    private boolean finalized;

    @Column(name = "FinalizedAt")
    private String finalizedAt;

    @Column(name = "Professional")
    private Professional professinal;

    @Column(name = "Service")
    private Service service;

    @Column(name = "Property")
    private Property property;

    public int getIdServer() {
        return (int)idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = (long) idServer;
    }

    public User getUserProf() {
        return this.userProf;
    }

    public void setUserProf(User professional) {
        this.userProf = professional;
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


    public Professional getProfessinal() {
        return professinal;
    }

    public void setProfessinal(Professional professinal) {
        this.professinal = professinal;
    }

    public int getProfessionalsId() {
        return professionalsId;
    }

    public void setProfessionalsId(int professionalsId) {
        this.professionalsId = professionalsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getServicesId() {
        return servicesId;
    }

    public void setServicesId(int servicesId) {
        this.servicesId = servicesId;
    }

    public String getDay() {
        return day;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public static List<Event> getEvents(){

        return new Select()
                .from(Event.class)
                .orderBy("StartAt ASC")
                .execute();
    }
}
