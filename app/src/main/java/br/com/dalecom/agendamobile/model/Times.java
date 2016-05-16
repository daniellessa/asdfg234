package br.com.dalecom.agendamobile.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by daniellessa on 25/03/16.
 */
public class Times {

    private Calendar startAt;
    private Calendar endsAt;
    private String imagePerfil;
    private String userName;
    private boolean free;
    private int viewType;
    private Event event;


    public Calendar getStartAt() {
        return startAt;
    }

    public void setStartAt(Calendar startAt) {
        this.startAt = startAt;
    }

    public Calendar getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Calendar endsAt) {
        this.endsAt = endsAt;
    }

    public String getImagePerfil() {
        return imagePerfil;
    }

    public void setImagePerfil(String imagePerfil) {
        this.imagePerfil = imagePerfil;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
