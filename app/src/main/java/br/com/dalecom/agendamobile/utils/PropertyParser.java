package br.com.dalecom.agendamobile.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;


public class PropertyParser {

    List<Property> properties;
    private JsonArray jsonArray;

    public PropertyParser(JsonArray jsonArray) {
        properties = new ArrayList<>();
        this.jsonArray = jsonArray;
    }

    public List parseFullProperty() {



        properties.clear();

        for (JsonElement jsonElement : jsonArray) {

            Property property = new Property();
            JsonObject data = jsonElement.getAsJsonObject();

            property.setIdServer(data.get("id").getAsInt());
            property.setPin(data.get("pin").getAsString());
            property.setName(data.get("name").getAsString());
            property.setPhoto_path(data.get("photo_path").getAsString());
            property.setBucketPath(data.get("bucket_name").getAsString());
            property.setOpenDay(data.get("open_day").getAsString());
            property.setOpenHour(data.get("open_hour").getAsString());
            property.setPhone(data.get("phone").getAsString());
            property.setStreet(data.get("street").getAsString());
            property.setNumber(data.get("number").getAsString());
            property.setCity(data.get("city").getAsString());
            property.setLat(data.get("lat").getAsFloat());
            property.setLng(data.get("lng").getAsFloat());


            if(!data.get("compl").isJsonNull()){
                property.setInfo(data.get("compl").getAsString());
            }



            properties.add(property);

        }

        Collections.sort(properties, new Comparator<Property>() {
            @Override
            public int compare(Property s1, Property s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });

        return properties;
    }


}
