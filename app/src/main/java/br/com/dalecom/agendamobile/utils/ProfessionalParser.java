package br.com.dalecom.agendamobile.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


        int backCategory = 0;
        users.clear();
        //provisorio


        for (JsonElement jsonElement : jsonArray) {

            User user = new User();
            JsonObject data = jsonElement.getAsJsonObject();

            if(data.getAsJsonObject("professions").get("id").getAsInt() != backCategory){
                int idCategory = data.getAsJsonObject("professions").get("id").getAsInt();
                String nameCategory = data.getAsJsonObject("professions").get("name").getAsString();
                createHeader(idCategory,nameCategory);
                backCategory = (data.getAsJsonObject("professions").get("id").getAsInt());
            }



            //mandatories
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
                startAt.setTime(format.parse(data.get("startAt").getAsString()));
                professional.setStartAt(startAt);

                Calendar endsAt = Calendar.getInstance();
                startAt.setTime(format.parse(data.get("endsAt").getAsString()));
                professional.setEndsAt(endsAt);

                Calendar split = Calendar.getInstance();
                startAt.setTime(format.parse(data.get("split").getAsString()));
                professional.setSplit(split);

                Calendar interval = Calendar.getInstance();
                startAt.setTime(format.parse(data.get("interval").getAsString()));
                professional.setInterval(interval);

                Calendar startLunchAt = Calendar.getInstance();
                startAt.setTime(format.parse(data.get("startLunchAt").getAsString()));
                professional.setStartLaunchAt(startLunchAt);

                Calendar endsLunchAt = Calendar.getInstance();
                startAt.setTime(format.parse(data.get("endsLunchAt").getAsString()));
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

                Log.d(LogUtils.TAG, "List item: " + user.getName());
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

    private void createHeader(int category, String name){

        User initial = new User();
        Professional init = new Professional();
        init.setCategory(category);
        init.setProfessionName(name);
        init.setViewType(0);
        initial.setProfessional(init);
        users.add(initial);
    }
}
