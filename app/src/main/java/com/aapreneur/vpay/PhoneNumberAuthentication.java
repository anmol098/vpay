package com.aapreneur.vpay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.aapreneur.vpay.Resources.Configuration;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
public class PhoneNumberAuthentication extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(PhoneNumberAuthentication.this, Main2Activity.class));
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.FirebaseLoginTheme)
                            .setLogo(R.mipmap.ic_launcher)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                    ))
                            .setIsSmartLockEnabled(false)
                            .setTosUrl("http://www.aapreneur.com/vpay/terms.html")
                            .setPrivacyPolicyUrl("http://www.aapreneur.com/vpay/policy.html")
                            .build(),

                    RC_SIGN_IN);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                new ReadData1().execute();
                /*startActivity(new Intent(PhoneNumberAuthentication.this,FirstSignup.class));
                finish();*/
                //return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }
    class ReadData1 extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String id,result,status;
        FirebaseAuth mAuth;

        @Nullable
        @Override
        protected Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if(user!= null) {
                id = user.getUid();
            }
            JSONArray jsonArray = Configuration.readData(id);
            try {
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                result = innerObject.getString("result");
                                status = innerObject.getString("status");

                                Log.w("myApp", result);
                                Log.w("myApp", status);
                            }
                        }
                    }
                } else {

                    result = "invalid";
                    status = "invalid";

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result.equals("1")&&status.equals("1")) {
                startActivity(new Intent(PhoneNumberAuthentication.this,Main2Activity.class));
                finish();
            } else if(result.equals("0")){
                startActivity(new Intent(PhoneNumberAuthentication.this,FirstSignup.class));
                finish();

            } else if(result.equals("1")&&status.equals("0")){
                startActivity(new Intent(PhoneNumberAuthentication.this,bank_details.class));
                finish();
            }else if(result.equals("invalid")&&status.equals("invalid")){
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
            }
        }
    }
}