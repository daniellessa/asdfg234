package br.com.dalecom.agendamobile.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

            service.setIdServer(data.get("id").getAsInt());
            service.setTitle(data.get("name").getAsString());
            service.setHours(data.get("hours").getAsInt());
            service.setMinutes(data.get("minutes").getAsInt());
            service.setPrice(new Float(data.get("price").getAsFloat()));

            if(!data.get("old_price").isJsonNull())
                service.setOldPrice(new Float(data.get("old_price").getAsFloat()));

            if(!data.get("info").isJsonNull())
                service.setInfo(data.getAsJsonObject("services").get("info").getAsString());

            services.add(service);

        }

        Collections.sort(services, new Comparator<Service>() {
            @Override
            public int compare(Service s1, Service s2) {
                return s1.getTitle().compareToIgnoreCase(s2.getTitle());
            }
        });

        return services;
    }


}
