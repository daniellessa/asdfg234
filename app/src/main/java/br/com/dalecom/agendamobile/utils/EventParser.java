package br.com.dalecom.agendamobile.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.User;


public class EventParser {

    List<Event> events;
    private JsonArray jsonArray;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventParser(JsonArray jsonArray) {
        events = new ArrayList<>();
        this.jsonArray = jsonArray;
    }

    public List parseFullEvents() {

        events.clear();

        for (JsonElement jsonElement : jsonArray) {

            Event event = new Event();
            JsonObject data = jsonElement.getAsJsonObject();

            try {
                event.setIdServer(data.get("id").getAsInt());

                Service service = new Service();
                service.setIdServer(data.getAsJsonObject("services").get("id").getAsInt());
                service.setTitle(data.getAsJsonObject("services").get("name").getAsString());
                service.setHours(data.getAsJsonObject("services").get("hours").getAsInt());
                service.setMinutes(data.getAsJsonObject("services").get("minutes").getAsInt());
                service.setPrice(data.getAsJsonObject("services").get("price").getAsFloat());
                service.setPropertyId(data.getAsJsonObject("services").get("property").getAsInt());
                if(!data.getAsJsonObject("services").get("info").isJsonNull())
                    service.setInfo(data.getAsJsonObject("services").get("info").getAsString());
                event.setService(service);

                User user = new User();
                user.setIdServer(data.getAsJsonObject("users").get("id").getAsInt());
                user.setName(data.getAsJsonObject("users").get("name").getAsString());
                user.setEmail(data.getAsJsonObject("users").get("email").getAsString());
                if(!data.getAsJsonObject("users").get("sex").isJsonNull())
                    user.setSex(data.getAsJsonObject("users").get("sex").getAsString());
                if(!data.getAsJsonObject("users").get("bucket_name").isJsonNull())
                    user.setBucketPath(data.getAsJsonObject("users").get("bucket_name").getAsString());
                if(!data.getAsJsonObject("users").get("photo_path").isJsonNull())
                    user.setPhotoPath(data.getAsJsonObject("users").get("photo_path").getAsString());
                event.setUser(user);

                User userProf = new User();
                userProf.setIdServer(data.getAsJsonObject("professionals").getAsJsonObject("users").get("id").getAsInt());
                userProf.setRegistrationId(data.getAsJsonObject("professionals").getAsJsonObject("users").get("registration_id").getAsString());
                userProf.setEmail(data.getAsJsonObject("professionals").getAsJsonObject("users").get("email").getAsString());
                userProf.setSex(data.getAsJsonObject("professionals").getAsJsonObject("users").get("sex").getAsString());
                userProf.setBucketPath(data.getAsJsonObject("professionals").getAsJsonObject("users").get("bucket_name").getAsString());
                userProf.setPhotoPath(data.getAsJsonObject("professionals").getAsJsonObject("users").get("photo_path").getAsString());
                event.setUserProf(userProf);


                event.setStartAt(Calendar.getInstance());
                String startAt = data.get("startAt").getAsString();
                event.getStartAt().setTime(format.parse(startAt));

                event.setEndsAt(Calendar.getInstance());
                String endsAt = data.get("endsAt").getAsString();
                event.getEndsAt().setTime(format.parse(endsAt));

                event.setStatus(data.get("status").getAsString());
                event.setFinalized(data.get("finalized").getAsBoolean());

//                if(!data.get("finalizedAt").isJsonNull())
//                    event.setFinalizedAt(Calendar.getInstance());
//                    event.getFinalizedAt().setTime(format.parse(data.get("finalizedAt").getAsString()));

                events.add(event);

            }
            catch (UnsupportedOperationException e)
            {
                Log.e(LogUtils.TAG, "Catch 1: " + e);
            }
            catch (ParseException e){
                Log.e(LogUtils.TAG, "Catch 2: " + e);
            }
        }

        return events;
    }

}
