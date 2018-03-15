package com.milvum.overseer;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;

/**
 * .
 */

public class HttpClient {
    private static final String TAG = "Beg";
    private RequestQueue requestQueue;

    public HttpClient(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public Future<Void> beg(String address) {
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("payload", address);
        } catch (JSONException e) {
            Log.wtf(TAG, "JSON Exception", e);
        }

        int method = Request.Method.POST;
        String url = BuildConfig.BEG_ADDRESS;

        RequestFuture<Void> future = RequestFuture.newFuture();
        Log.d(TAG, "Sending: " + jsonParams.toString());

        requestQueue.add(new ParsedJsonRequest(method, url, jsonParams.toString(), future, future));

        return future;
    }

    private class ParsedJsonRequest extends JsonRequest<Void> {
        private ParsedJsonRequest(int method, String url, String requestBody, Response.Listener<Void> responseListener, Response.ErrorListener errorListener) {
            super(method, url, requestBody, responseListener, errorListener);
        }

        @Override
        protected Response<Void> parseNetworkResponse(NetworkResponse response) {
            Log.d(TAG, "Received response " + response);
            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        }
    }
}
