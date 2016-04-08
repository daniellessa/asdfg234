package br.com.dalecom.agendamobile.utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import br.com.dalecom.agendamobile.adapters.expandable.Header;

public class HeaderParser {

    List<Header> headers;
    private JsonArray jsonArray;

    public HeaderParser(JsonArray jsonArray) {
        headers = new ArrayList<>();
        this.jsonArray = jsonArray;
    }

    public List parseFullCategory() {



        headers.clear();


        for (JsonElement jsonElement : jsonArray) {

            Header header = new Header();
            JsonObject data = jsonElement.getAsJsonObject();

                header.setId(data.get("id").getAsInt());
                header.setTitle(data.get("name").getAsString());

            headers.add(header);

        }

        return headers;
    }

}
