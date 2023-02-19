package edu.aubg.ics.util;


import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseParser {
    public JSONArray jsonParser(String json) {
        JSONObject response = new JSONObject(json);
        JSONObject result = response.getJSONObject("result");
        JSONArray tags = result.getJSONArray("tags");
        JSONArray newTags = new JSONArray();

        for (Object tagObject : tags) {
            JSONObject tag = (JSONObject) tagObject;
            if (tag.getDouble("confidence") < 50.0) {
                break;
            }
            newTags.put(tag);
        }

        return newTags;
    }
}
