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

    @POST("/events")
    void postEvent(@Body Event event, Callback<JsonObject> callback);

    @GET("/properties")
    void getProperties(@Query("pin") int pin, Callback<JsonObject> callback);

    @GET("/professionals")
    void getProfessionals(@Query("property_id") int propertyId, Callback<JsonObject> callback);

    @GET("/events")
    void getEvents(@Query("professionals_id") int professionals_id, @Query("day") String day, Callback<JsonObject> callback);

    @GET("/professions")
    void getCategories(Callback<JsonObject> callback);

    @GET("/services")
    void getServicesForProfessional(@Query("professional_id") int professional_id,Callback<JsonObject> callback);

    @POST("/events")
    void postExams(@Body List<Event> events, Callback<JsonObject> callback);

    @POST("/update-image")
    void postImage(@Body User user, Callback<JsonObject> callback);

    @FormUrlEncoded
    @POST("/exams/status")
    void getHistoryStatus(@Field("exams") ArrayList examIds, Callback<JsonArray> callback);

    @FormUrlEncoded
    @POST("/user/firstPassword")
    void setPasswordRefreshed(@Field("password") String password, Callback<JsonArray> callback);
}
