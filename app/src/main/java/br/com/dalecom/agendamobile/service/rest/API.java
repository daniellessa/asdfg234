package br.com.dalecom.agendamobile.service.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.User;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by guilhermeduartemattos on 10/21/15.
 */
public interface API {

    @FormUrlEncoded
    @POST("/auth")
    void login(@Field("email") String email, @Field("password") String password, Callback<JsonObject> callback);

    @POST("/users")
    void postUser(@Body User user, Callback<JsonObject> callback);

    @POST("/authGoogleOrFacebook")
    void loginGoogleOrFacebook(@Body User user, Callback<JsonObject> callback);

    @POST("/events")
    void postEvent(@Body Event event, Callback<JsonObject> callback);

    @POST("/event_cancel")
    void cancelEvent(@Body Event event, Callback<JsonObject> callback);

    @GET("/properties")
    void getProperties(@Query("pin") String pin, Callback<JsonObject> callback);

    @GET("/professionals")
    void getProfessionals(@Query("property_id") int propertyId, @Query("service_id") int serviceId, Callback<JsonObject> callback);

    @GET("/events")
    void getEvents(@Query("professionals_id") int professionals_id, @Query("day") String day, Callback<JsonObject> callback);

    @GET("/events_appointments")
    void getAppointments( Callback<JsonObject> callback);

    @GET("/professions")
    void getCategories(Callback<JsonObject> callback);

    @GET("/events_not_expired")
    void getEventsNotExpired(Callback<JsonObject> callback);

    @GET("/events_expired")
    void getEventsExpired(Callback<JsonObject> callback);

    @GET("/services")
    void getServicesForProperty(@Query("property_id") int professional_id,Callback<JsonObject> callback);

    @POST("/events")
    void postExams(@Body List<Event> events, Callback<JsonObject> callback);

    @POST("/update-image")
    void postImage(@Body User user, Callback<JsonObject> callback);

    @POST("/new_association")
    void notifyNewAssociation(@Query("property_id") int propertyId, Callback<JsonObject> callback);

    @POST("/new_event")
    void notifyNewEvent(@Query("professional_id") int professionalId, Callback<JsonObject> callback);

    @FormUrlEncoded
    @POST("/exams/status")
    void getHistoryStatus(@Field("exams") ArrayList examIds, Callback<JsonArray> callback);

    @FormUrlEncoded
    @POST("/user/firstPassword")
    void setPasswordRefreshed(@Field("password") String password, Callback<JsonArray> callback);
}
