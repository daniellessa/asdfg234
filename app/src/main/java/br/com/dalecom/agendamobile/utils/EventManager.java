package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;


/**
 * Created by viniciuslima on 10/19/15.
 */
@Singleton
public class EventManager {
    Context mContext;
    private Event mEvent;
    private User currentUser;
    private User currentUserProf;
    private Professional currentProfessional;
    private Calendar dateSelected;
    private Service currentService;
    private String currentDay;
    private String currentStartAt;
    private String currentEndsAt;
    private Property currentProperty;

    @Inject public static SharedPreference sharedPreference;

    @Inject
    public EventManager(Context mContext) {
        ( (AgendaMobileApplication) mContext).getAppComponent().inject(this);
        this.mContext = mContext;
    }


    public void startNewEvent(Calendar date) {
        this.mEvent = new Event();
        this.dateSelected = date;
        this.currentDay = DateHelper.toStringSql(date);
        String dateSql = DateHelper.convertDateToStringSql(date);
        this.mEvent.setStartAt(dateSql);
        setEventCurrentUser();
    }

    public void setEventCurrentUser() {
            User user = sharedPreference.getCurrentUser();
            this.currentUser = user;
            this.mEvent.setUser(user);
    }

    public void setUserProfIntoEvent(User user) {
        this.currentUserProf = user;
        this.currentProfessional = user.getProfessional();
    }

    public User getCurrentUserProfessional(){
        return currentUserProf;
    }

    public Professional getCurrentProfessional() {
        return currentProfessional;
    }

    public void setCurrentProfessional(Professional currentProfessional) {
        this.currentProfessional = currentProfessional;
    }

    public void setServiceIntoEvent(Service service) {
        this.currentService = service;
    }

    public Service getCurrentService(){
        return this.currentService;
    }

    public Calendar getDateSelected() {
        return dateSelected;
    }

    public String getCurrentStartAt() {
        return currentStartAt;
    }

    public void setCurrentStartAt(String currentStartAt) {
        this.currentStartAt = currentStartAt;
    }

    public String getCurrentEndsAt() {
        return currentEndsAt;
    }

    public void setCurrentEndsAt(String currentEndsAt) {
        this.currentEndsAt = currentEndsAt;
    }

    public Event getEvent() {
        return mEvent;
    }

    public void setDateSelected(Calendar dateSelected) {
        this.dateSelected = dateSelected;
    }

    public Property getCurrentProperty() {
        return currentProperty;
    }

    public void setCurrentProperty(Property currentProperty) {
        this.currentProperty = currentProperty;
    }

    public void finalizeEvent() {

//
        this.mEvent.setUserId(currentUser.getIdServer());
        this.mEvent.setProfessionalsId(currentUserProf.getIdServer());
        this.mEvent.setServicesId(currentService.getIdServer());
        this.mEvent.setDay(DateHelper.toStringSql(dateSelected));
        this.mEvent.setStartAt(currentStartAt);
        this.mEvent.setEndsAt(currentEndsAt);
        this.mEvent.setStatus(S.STATUS_PENDING);
        this.mEvent.setFinalized(false);

        //this.mEvent.save();

    }

    public User getUserProf() {
        if ( this.mEvent != null )
            return this.mEvent.getUserProf();

        return null;
    }

    public void removeService(Service service) {
        if ( this.mEvent != null )
        {
            this.mEvent.setService(null);
        }
    }

    public void clear(){
        this.mEvent.setUser(null);
        this.mEvent.setUserProf(null);
        this.mEvent.setProfessinal(null);
        this.mEvent.setService(null);
        this.mEvent.setStartAt(null);
        this.mEvent.setEndsAt(null);
        this.mEvent.setStatus(null);
        this.mEvent.setFinalized(false);
    }

}
