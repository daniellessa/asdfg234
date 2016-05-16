package br.com.dalecom.agendamobile.service.rest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by guilhermeduartemattos on 10/21/15.
 */
public class RestClient {

    private static final long HTTP_TIMEOUT_MILI = 20000;
    private static API api;

    @Inject
    public SharedPreference sharedPreference;


    @Inject
    public RestClient(Context mContext) {
        ( (AgendaMobileApplication) mContext).getAppComponent().inject(this);
        setupEndPoint();
    }

    private void setupEndPoint() {

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(HTTP_TIMEOUT_MILI, TimeUnit.MILLISECONDS);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        RestAdapter restAdapter =  new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {

                        if ( sharedPreference.hasUserToken() )
                        {
                            request.addHeader("Authorization","Bearer " + sharedPreference.getUserToken());
                        }

                    }
                })
                .setConverter(new GsonConverter(gson))
                .setEndpoint(S.END_POINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(client))
                .build();


        api = restAdapter.create(API.class);
    }

    public void postUser(User user, Callback callback) {
        api.postUser(user, callback);
    }


    public void loginGoogleOrFacebbok(User user, Callback callback) {
        api.loginGoogleOrFacebook(user, callback);
    }

    public void postEvent(Event event, Callback callback) {
        api.postEvent(event, callback);
    }

    public void cancelEvent(Event event, Callback callback) {
        api.cancelEvent(event, callback);
    }


    public void postImage(User user, Callback callback) {
        api.postImage(user, callback);
    }

    public void updatePasswordRefresh(String password,Callback callback) {
        api.setPasswordRefreshed(password, callback);
    }

    public void login(String email, String password, Callback callback) {
        api.login(email, password, callback);
    }

    public void getProperties(String pin, Callback callback) {
        api.getProperties(pin, callback);
    }

    public void getProfessionals(int propertyId, int serviceId, Callback callback) {
        api.getProfessionals(propertyId, serviceId, callback);
    }

    public void getEvents(int userProf_id, String day, Callback callback) {
        api.getEvents(userProf_id, day, callback);
    }

    public void getEventsNotExpired(Callback callback){
        api.getEventsNotExpired(callback);
    }

    public void getEventsExpired(Callback callback){
        api.getEventsExpired(callback);
    }

    public void getAppointments(Callback callback) {
        api.getAppointments(callback);
    }

    public void getCategories(Callback callback) {
        api.getCategories(callback);
    }

    public void getServiceForProperty(int professionalId, Callback callback) {
        api.getServicesForProperty(professionalId, callback);
    }

    public void notifyNewAssociation(int propertyId, Callback callback) {
        api.notifyNewAssociation(propertyId, callback);
    }

    public void notifyNewEvent(int professionalId, Callback callback) {
        api.notifyNewEvent(professionalId, callback);
    }



}
