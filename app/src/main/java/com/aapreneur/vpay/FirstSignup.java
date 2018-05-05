package com.aapreneur.vpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aapreneur.vpay.Resources.Configuration;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class FirstSignup extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextName;
    String email;
    String name;
    String id;
    String profile_photo;
    String mobile;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_signup);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextName = (EditText) findViewById(R.id.name);
        id = user.getUid();
        profile_photo ="https://img7.androidappsapk.co/300/7/3/a/com.profile.admires_stalkers_unknown.png";
        mobile = user.getPhoneNumber();

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = editTextEmail.getText().toString().trim();
                name = editTextName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Your Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateprofile();
                Submit();
            }
        });
    }
    private void updateprofile()
    {
        auth = FirebaseAuth.getInstance();
        if(user!=null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editTextName.getText().toString())
                    .setPhotoUri(Uri.parse("https://img7.androidappsapk.co/300/7/3/a/com.profile.admires_stalkers_unknown.png"))
            .build();

            user.updateProfile(profileUpdates);
        }
    }
    private void Submit() {
        final ProgressDialog loading = ProgressDialog.show(this,"Registering ...","Please wait...",false,false);

        final String userName = name;
        final String userEmail = email;
        final String userId = id;
        final String userMobile = mobile;
        final String userImage = profile_photo;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        editTextName.setText(null);
                        editTextEmail.setText(null);
                        startActivity(new Intent(FirstSignup.this, bank_details.class));
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Configuration.KEY_ACTION,"insert");
                params.put(Configuration.KEY_ID,userId);
                params.put(Configuration.KEY_NAME,userName);
                params.put(Configuration.KEY_EMAIL,userEmail);
                params.put(Configuration.KEY_MOBILE,userMobile);
                params.put(Configuration.KEY_IMAGE,userImage);
                params.put(Configuration.KEY_RESULT,"1");
                params.put(Configuration.KEY_FEE,"0.015");
                return params;
            }

        };

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(stringRequest);
    }
}