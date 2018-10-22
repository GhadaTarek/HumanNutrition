package com.amg.humannutrition.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.amg.humannutrition.Activity.LoginActivity;
import com.amg.humannutrition.Activity.MainActivity;
import com.amg.humannutrition.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by moata on 3/10/2018.
 */

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest tokenRequest = new StringRequest(Request.Method.POST,
                getString(R.string.Resource_Address) + getString(R.string.UpdateToken_Address),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (Objects.equals(response, "Done"))
                        {
                            //do nothing
                            //Log.d(getString(R.string.TAG), "refreshToken: " + response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do nothing
                //Log.e(getString(R.string.TAG), "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",Get_Email());
                params.put("token", refreshedToken);
                return params;
            }

        };
        // Adding request to request queue
        queue.add(tokenRequest);
    }
    String Get_Email() {
        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
        String Email = sharedPref.getString("Email", null);
        if (Email != null) {
            return Email;
        }
        return "";
    }
}
