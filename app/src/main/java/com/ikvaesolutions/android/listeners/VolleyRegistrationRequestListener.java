package com.ikvaesolutions.android.listeners;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by amarilindra on 25/04/17.
 */

public interface VolleyRegistrationRequestListener {
    void onError(String error, String statusCode);
    void onResponse(JSONObject response);
}
