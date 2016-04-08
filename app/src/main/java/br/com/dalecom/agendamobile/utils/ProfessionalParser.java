package br.com.dalecom.agendamobile.utils;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.User;



public class ProfessionalParser {

    List<User> users;
    private JsonArray jsonArray;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public ProfessionalParser(JsonArray jsonArray) {
        users = new ArrayList<>();
        this.jsonArray = jsonArray;
    }

    public List parseFullProfessionas() {

        users.clear();

        for (JsonElement jsonElement : jsonArray) {

            User user = new User();
            JsonObject data = jsonElement.getAsJsonObject();


            try {
                user.setIdServer(data.getAsJsonObject("users").get("id").getAsInt());
                user.setEmail(data.getAsJsonObject("users").get("email").getAsString());
                user.setName(data.getAsJsonObject("users").get("name").getAsString());
                user.setSex(data.getAsJsonObject("users").get("sex").getAsString());

                if(!data.getAsJsonObject("users").get("registration_id").isJsonNull())
                    user.setRegistrationId(data.getAsJsonObject("users").get("registration_id").getAsString());

                if ( !data.getAsJsonObject("users").get("bucket_name").isJsonNull() )
                    user.setBucketPath(data.getAsJsonObject("users").get("photo_path").getAsString());

                if ( !data.getAsJsonObject("users").get("photo_path").isJsonNull() )
                    user.setPhotoPath(data.getAsJsonObject("users").get("photo_path").getAsString());

                Professional professional = new Professional();

                professional.setProperties(data.getAsJsonObject("properties").get("id").getAsInt());
                professional.setCategory(data.getAsJsonObject("professions").get("id").getAsInt());
                professional.setProfessionName(data.getAsJsonObject("professions").get("name").getAsString());

                Calendar startAt = Calendar.getInstance();
                Date dateStart = format.parse(data.get("startAt").getAsString());
                startAt.set(Calendar.HOUR_OF_DAY, dateStart.getHours());
                startAt.set(Calendar.MINUTE, dateStart.getMinutes());
                startAt.set(Calendar.SECOND, 0);
                professional.setStartAt(startAt);


                Calendar endsAt = Calendar.getInstance();
                Date dateEnds = format.parse(data.get("endsAt").getAsString());
                endsAt.set(Calendar.HOUR_OF_DAY, dateEnds.getHours());
                endsAt.set(Calendar.MINUTE, dateEnds.getMinutes());
                endsAt.set(Calendar.SECOND, 0);
                professional.setEndsAt(endsAt);

                Calendar split = Calendar.getInstance();
                Date dateSplit = format.parse(data.get("split").getAsString());
                split.set(Calendar.HOUR_OF_DAY, dateSplit.getHours());
                split.set(Calendar.MINUTE, dateSplit.getMinutes());
                split.set(Calendar.SECOND, 0);
                professional.setSplit(split);

                Calendar interval = Calendar.getInstance();
                Date dateInterval = format.parse(data.get("interval").getAsString());
                interval.set(Calendar.HOUR_OF_DAY, dateInterval.getHours());
                interval.set(Calendar.MINUTE, dateInterval.getMinutes());
                interval.set(Calendar.SECOND, 0);
                professional.setInterval(interval);

                Calendar startLunchAt = Calendar.getInstance();
                Date dateStartLunch = format.parse(data.get("startLunchAt").getAsString());
                startLunchAt.set(Calendar.HOUR_OF_DAY, dateStartLunch.getHours());
                startLunchAt.set(Calendar.MINUTE, dateStartLunch.getMinutes());
                startLunchAt.set(Calendar.SECOND, 0);
                professional.setStartLaunchAt(startLunchAt);

                Calendar endsLunchAt = Calendar.getInstance();
                Date dateEndsLunch = format.parse(data.get("endsLunchAt").getAsString());
                endsLunchAt.set(Calendar.HOUR_OF_DAY, dateEndsLunch.getHours());
                endsLunchAt.set(Calendar.MINUTE, dateEndsLunch.getMinutes());
                endsLunchAt.set(Calendar.SECOND, 0);
                professional.setEndsLaunchAt(endsLunchAt);

                professional.setWorkSunday(data.get("workSunday").getAsBoolean());
                professional.setWorkMonday(data.get("workMonday").getAsBoolean());
                professional.setWorkTuesday(data.get("workMonday").getAsBoolean());
                professional.setWorkWednesday(data.get("workWednesday").getAsBoolean());
                professional.setWorkThursday(data.get("workThursday").getAsBoolean());
                professional.setWorkFriday(data.get("workFriday").getAsBoolean());
                professional.setWorkSaturday(data.get("workSaturday").getAsBoolean());
                professional.setViewType(1);
                user.setProfessional(professional);

                users.add(user);

            }
            catch (UnsupportedOperationException e)
            {
                Log.d(LogUtils.TAG, "Catch 1: " + e);
                continue;
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(LogUtils.TAG, "Catch 2: " + e);
            }
        }

        return users;
    }


}
