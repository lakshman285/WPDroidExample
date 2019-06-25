package com.ikvaesolutions.android.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ikvaesolutions.android.app.AppController;
import com.ikvaesolutions.android.constants.ApiConstants;
import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.listeners.VolleyJSONArrayRequestListener;
import com.ikvaesolutions.android.listeners.VolleyJSONObjectRequestListener;
import com.ikvaesolutions.android.listeners.VolleyRegistrationRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amarilindra on 25/04/17.
 */

public class VolleyUtils {

    static String TAG = "VolleyUtils";

    private static final String registrationSlug = "register";
//    public static final String getRegistrationUrl = ApiConstants.BASE_URL + registrationSlug;

    /*
    Common method for POST request
     */

    public static void makePostRequest(HashMap<String, String> params, final String url, final VolleyRegistrationRequestListener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String body;
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    body = "error";
                                }
                            } else {
                                body = "error";
                            }
                            listener.onError(body, statusCode);
                        }catch (Exception e) {
                            listener.onError("Exception","unknown");
                        }
                    }

                }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
       // AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }



    public static void makeJSONArrayRequest(JSONArray params, final String url, int method, String requestType, final VolleyJSONArrayRequestListener listener) {

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (method, url, params, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        try {
                            CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, error.getMessage());
                            String body;
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    body = "error";
                                }
                            } else {
                                body = "error";
                            }
                            listener.onError(body, statusCode);
                        }catch (Exception e) {
                            listener.onError("Exception","unknown");
                        }
                    }

                }) {

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    String extraText = "</script>";

                    if(jsonString.contains(extraText)) {
                        String jsonStrings[] = jsonString.split(extraText,2);
                        jsonString = jsonStrings[1];
                    }

                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, requestType);
    }

    public static void makeJSONObjectRequest(JSONObject params, final String url, int method, final String requestType, final boolean customHeaders, final VolleyJSONObjectRequestListener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        try {
                            CommonUtils.writeLog(AppConstants.ERROR_LOG, TAG, error.getMessage());
                            String body;
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    body = "error";
                                }
                            } else {
                                body = "error";
                            }
                            listener.onError(body, statusCode);
                        }catch (Exception e) {
                            listener.onError("Exception","unknown");
                        }
                    }

                }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
//
//                    Log.e("- ############ ---", ""+jsonString);
//
//                    String extraText = "</script>";
//
//                    if(jsonString.contains(extraText)) {
//                        String jsonStrings[] = jsonString.split(extraText,2);
//                        jsonString = jsonStrings[1];
//                    }

                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                if(customHeaders) {
                    headers.put(ApiConstants.GET_INTERNAL_LINK_HEADER_KEY, requestType);
                }
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, requestType);
    }
}
