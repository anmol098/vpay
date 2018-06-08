package com.aapreneur.vpay.Resources;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by Anmol Pratap Singh on 11-03-2018.
 */

public class Configuration {
    public static final String TAG = "TAG";


    public static final String APP_SCRIPT_WEB_APP_URL = "https://script.google.com/macros/s/AKfycbx43JUOlcT9h0nAIaydWbenL8XXO8jbjbxnB1w2zsSLpEYPfI4/exec";
    public static final String ADD_USER_URL = APP_SCRIPT_WEB_APP_URL;
    public static final String LIST_USER_URL = APP_SCRIPT_WEB_APP_URL+"?action=readAll";

    public static final String KEY_ID = "uId";
    public static final String KEY_NAME = "uName";
    public static final String KEY_EMAIL = "uEmail";
    public static final String KEY_MOBILE = "uMobile";
    public static final String KEY_IMAGE = "uImage";
    public static final String KEY_RESULT = "uResult";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_TXN_AMOUNT = "txnAmount";
    public static final String KEY_PAYBACK = "payback";
    public static final String KEY_FEES = "fees";
    public static final String KEY_ACC_NAME = "aName";
    public static final String KEY_ACC_NUMBER = "aNumber";
    public static final String KEY_ACC_IFSC = "aIfsc";
    public static final String KEY_ACC_STATUS = "aStatus";
    public static final String KEY_ORDER_ID = "uOrderid";
    public static final String KEY_MODE = "mode";

    //firebase storage constants
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";



    public  static final String KEY_ACTION = "action";

    private static Response response;

    public static JSONArray readData(String email) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=read&uId="+email)
                    .build();
            response = client.newCall(request).execute();
            // Log.e(TAG,"response from gs"+response.body().string());
            return new JSONArray(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
    public static JSONArray recentTransaction(String id) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=recent&uId="+id)
                    .build();
            response = client.newCall(request).execute();
            // Log.e(TAG,"response from gs"+response.body().string());
            return new JSONArray(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
    public static JSONObject updateData(String id, String status) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=update&uId="+id+"&status="+status)
                    .build();
            response = client.newCall(request).execute();
            //    Log.e(TAG,"response from gs"+response.body().string());
            return new JSONObject(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
    public static JSONObject updateRef(String id, String status,String name) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=addRef&orderId="+id+"&refId="+status+"&name="+name)
                    .build();
            response = client.newCall(request).execute();
            //    Log.e(TAG,"response from gs"+response.body().string());
            return new JSONObject(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
    public static JSONArray readAccount(String email) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=getAccount&id="+email)
                    .build();
            response = client.newCall(request).execute();
            // Log.e(TAG,"response from gs"+response.body().string());
            return new JSONArray(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
    public static JSONArray readProfile(String email) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(APP_SCRIPT_WEB_APP_URL+"?action=getProfile&id="+email)
                    .build();
            response = client.newCall(request).execute();
            // Log.e(TAG,"response from gs"+response.body().string());
            return new JSONArray(response.body().string());


        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "recieving null " + e.getLocalizedMessage());
        }
        return null;
    }
}
