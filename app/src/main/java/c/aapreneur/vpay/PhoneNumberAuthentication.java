package c.aapreneur.vpay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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

import c.aapreneur.vpay.Resources.Configuration;
public class PhoneNumberAuthentication extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            if (isInternetOn()) {
                new ReadData1().execute();
            }
            else {
                startActivity(new Intent(PhoneNumberAuthentication.this, Main2Activity.class));
                finish();
            }

        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.FirebaseLoginTheme)
                            .setLogo(R.mipmap.ic_launcher)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
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
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(getApplicationContext(), " Not Connected Some functionality may be disabled", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
    class ReadData1 extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String id,result,status;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.progress,null);
            setContentView(view);


        }

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
                Toast.makeText(getApplicationContext(), " Not Connected Some functionality may be disabled", Toast.LENGTH_LONG).show();
                startActivity(new Intent(PhoneNumberAuthentication.this,Main2Activity.class));
                finish();
            }
        }
    }
}