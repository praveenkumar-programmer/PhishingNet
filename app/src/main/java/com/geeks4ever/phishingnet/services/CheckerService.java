package com.geeks4ever.phishingnet.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.geeks4ever.phishingnet.model.URLmodel;
import com.geeks4ever.phishingnet.model.repository.CommonRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckerService extends Service {


    private static final String TAG = "Phishing Net";


    CommonRepository repository;

    public RequestQueue queue;


    @Override
    public void onCreate() {
        super.onCreate();

        repository = CommonRepository.getInstance(getApplication());
        queue = Volley.newRequestQueue(this);
        if (queue == null)
            queue = Volley.newRequestQueue(this);

        repository.getCurrentUrl().observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
//                if(!strings.isEmpty())
//                    checkURL(strings.get(0));
            }
        });

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void checkURL(String s) {

        if (s.contains("instagram")) {
            s = instagramURLDecoder(s);
        }
        String finalS = s;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkGoogleSafeBrowsing(finalS);
            }
        });
    }

    private String instagramURLDecoder(String url) {
        String mainLink = url;
        try {
            String afterDecode = URLDecoder.decode(url, "UTF-8");
            Uri uri = Uri.parse(afterDecode);
            mainLink = uri.getQueryParameter("u");
        } catch (UnsupportedEncodingException unsupp) {
            Log.e(TAG, unsupp.toString());
        }
        return mainLink;
    }

    private void checkGoogleSafeBrowsing(String urlToCheck) {

        try {
            final String urlToCheck2 = urlToCheck;
            JSONObject json = getJsonObject(urlToCheck);
            String url = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyDVhCTR3IWUfteUGVugMEepE235_50TlLY";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if (response.has("matches")) {
                                repository.addURL(new URLmodel(URLmodel.BAD_URL, urlToCheck));
                            } else {
                                //check with machine learning API
                                machineLearningCheck(urlToCheck2);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.e("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };


            //queue.add(postRequest);
            //jsonObjectRequest.setTag(REQ_TAG);
            queue.add(jsonObjectRequest);
        } catch (JSONException jsx) {
            Log.d(TAG, jsx.toString());
        }

    }

    private void machineLearningCheck(String urlToCheck) {
        try {
            //url
            final String urlToCheck2 = urlToCheck;
            JSONObject urlObject = new JSONObject();
            urlObject.put("url", urlToCheck);
            String url = "https://phish-defender.herokuapp.com/api";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, urlObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            if (response.has("prediction")) {
                                try {
                                    int Result = response.getInt("prediction");
                                    if (Result == 1) {
                                        repository.addURL(new URLmodel(URLmodel.BAD_URL, urlToCheck));
                                    }
                                } catch (JSONException jsx) {
                                    Log.e(TAG, jsx.toString());
                                }

                                //showFloatingWindow();
                            }
                            else
                                repository.addURL(new URLmodel(URLmodel.GOOD_URL, urlToCheck));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.e("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };


            //queue.add(postRequest);
            //jsonObjectRequest.setTag(REQ_TAG);
            queue.add(jsonObjectRequest);

        } catch (Exception ignored) {

        }
    }


    private JSONObject getJsonObject(String url) throws JSONException {
        //main object
        JSONObject jsonObject = new JSONObject();

        //url
        JSONObject urlObject = new JSONObject();
        urlObject.put("url", url);

        List<JSONObject> urlList = new ArrayList<JSONObject>();
        urlList.add(urlObject);

        JSONArray urlArray = new JSONArray(urlList);

        //client
        JSONObject clientObject = new JSONObject();
        JSONObject clientJson = new JSONObject();
        clientJson.put("clientId", "Phish_Defender");
        clientJson.put("clientVersion", "1.5.2");
        //clientObject.put("client", clientJson);

        //threat
        JSONObject threatObject = new JSONObject();
        JSONObject threatJson = new JSONObject();
        threatJson.put("threatTypes", "SOCIAL_ENGINEERING");
        threatJson.put("platformTypes", "WINDOWS");
        threatJson.put("threatEntryTypes", "URL");
        threatJson.put("threatEntries", urlArray);
        //threatObject.put("threatInfo",threatJson);

        //final Object
        jsonObject.put("client", clientJson);
        jsonObject.put("threatInfo", threatJson);


        return jsonObject;
    }

}
