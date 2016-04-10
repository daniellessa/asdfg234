package br.com.dalecom.agendamobile.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.User;


public class ServicesParser {

    List<Service> services;
    private JsonArray jsonArray;

    public ServicesParser(JsonArray jsonArray) {
        services = new ArrayList<>();
        this.jsonArray = jsonArray;
    }

    public List parseFullServices() {



        services.clear();

        for (JsonElement jsonElement : jsonArray) {

            Service service = new Service();
            JsonObject data = jsonElement.getAsJsonObject();

            service.setIdServer(data.getAsJsonObject("services").get("id").getAsInt());
            service.setTitle(data.getAsJsonObject("services").get("name").getAsString());
            service.setHours(data.getAsJsonObject("services").get("hours").getAsInt());
            service.setMinutes(data.getAsJsonObject("services").get("minutes").getAsInt());
            service.setPrice(new Float(data.getAsJsonObject("services").get("price").getAsFloat()));

            if(!data.getAsJsonObject("services").get("info").isJsonNull())
            service.setInfo(data.getAsJsonObject("services").get("info").getAsString());

            services.add(service);

        }

        return services;
    }

}
