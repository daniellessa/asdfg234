package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;


/**
 * Created by viniciuslima on 10/19/15.
 */
@Singleton
public class EventManager {
    Context mContext;
    Event mEvent;
    User currentUser;
    User currentProfessional;
    Calendar dateSelected;
    Service currentService;
    String currentStartAt;
    String currentEndsAt;

    @Inject public static SharedPreference sharedPreference;

    @Inject
    public EventManager(Context mContext) {
        ( (AgendaMobileApplication) mContext).getAppComponent().inject(this);
        this.mContext = mContext;
    }


    public void startNewEvent(Calendar date) {
        this.mEvent = new Event();
        this.dateSelected = date;
        String dateSql = DateHelper.convertDateToStringSql(date);
        Log.d(LogUtils.TAG, "StartNewEvent: "+ dateSql);
        this.mEvent.setStartAt(dateSql);
        setEventCurrentUser();
    }

    public void setUserIntoEvent(User user) {
        this.mEvent.setUser(user);
    }


    public void setEventCurrentUser() {
            User user = sharedPreference.getCurrentUser();
            this.currentUser = user;
            this.mEvent.setUser(user);
    }

    public void setProfessionalIntoEvent(User professional) {
        this.mEvent.setProfessional(professional);
        this.currentProfessional = professional;
    }

    public void setServiceIntoEvent(Service service) {
        this.mEvent.setService(service);
        this.currentService = service;
    }

    public Calendar getDateSelected() {
        return dateSelected;
    }

    public void setDateSelected(Calendar dateSelected) {
        this.dateSelected = dateSelected;
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

    public void saveEvent() {


        User user = this.mEvent.getUser();
        user.save();

        User professional = this.mEvent.getProfessional();
        professional.save();

        Service service = this.mEvent.getService();


//      User user = User.getUserByServerId(this.currentUser.getIdServer());
//

        this.mEvent.setUser(user);
        this.mEvent.setProfessional(professional);
        this.mEvent.setService(service);
        this.mEvent.setStartAt(currentStartAt);
        this.mEvent.setEndsAt(currentEndsAt);
        this.mEvent.setStatus(Event.STATUS_AGENDADO);
        this.mEvent.setFinalized(false);
        this.mEvent.setToken(sharedPreference.getUserToken());
        this.mEvent.save();

    }

    public User getProfessional() {
        if ( this.mEvent != null )
            return this.mEvent.getProfessional();

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
        this.mEvent.setProfessional(null);
        this.mEvent.setService(null);
        this.mEvent.setStartAt(null);
        this.mEvent.setEndsAt(null);
        this.mEvent.setStatus(null);
        this.mEvent.setFinalized(false);
        this.mEvent.setToken(null);
    }

}
