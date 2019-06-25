package com.ikvaesolutions.android.listeners;

import org.json.JSONObject;

/**
 * Created by amarilindra on 29/06/17.
 */

public interface VolleyJSONObjectRequestListener {
    void onError(String error, String statusCode);
    void onResponse(JSONObject response);
}
