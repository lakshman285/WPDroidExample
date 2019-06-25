package com.ikvaesolutions.android.listeners;

import org.json.JSONArray;

/**
 * Created by amarilindra on 25/04/17.
 */

public interface VolleyJSONArrayRequestListener {
    void onError(String error, String statusCode);
    void onResponse(JSONArray response);
}
