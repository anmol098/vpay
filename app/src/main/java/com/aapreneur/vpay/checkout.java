package com.aapreneur.vpay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class checkout extends AppCompatActivity {

    TextView account,ifsc;
    TextView AmtPayback,fees,txnAmount,ordernumber,TVnumber;
    TextView date;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    Locale INR = new Locale("en", "IN");
    NumberFormat inrFormat = NumberFormat.getCurrencyInstance(INR);

    String paytmUrl="null",payuUrl="null";

    String name,number,IFSC,amount,fee,payback,email,phno,mode;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        SharedPreferences prefs = getSharedPreferences("pref_data", MODE_PRIVATE);
            email = prefs.getString("email", "No name defined");//"No name defined" is the default value.
            Log.e("email",email);


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mode = intent.getStringExtra("mode");
        number = intent.getStringExtra("number");
        IFSC = intent.getStringExtra("ifsc");
        amount = String.valueOf(inrFormat.format(intent.getDoubleExtra("amount",0)));
        fee = String.valueOf(inrFormat.format(intent.getDoubleExtra("fees",0)));
        payback = String.valueOf(inrFormat.format(intent.getDoubleExtra("payback",0)));


        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled())
            cacheExpiration = 0;

        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(checkout.this, "Fetch Succeeded",
                            Toast.LENGTH_SHORT).show();

                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();

                    paytmUrl=mFirebaseRemoteConfig.getString("paytm");
                    payuUrl=mFirebaseRemoteConfig.getString("payu");
                } else {
                    Toast.makeText(checkout.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        account = findViewById(R.id.bank);
        account.setText(number);

        ifsc = findViewById(R.id.ifsc);
        ifsc.setText(IFSC);

        txnAmount = findViewById(R.id.amount);
        txnAmount.setText(amount);

        fees = findViewById(R.id.fees);
        fees.setText(fee);

        AmtPayback = findViewById(R.id.payback);
        AmtPayback.setText(payback);

        date = findViewById(R.id.date);
        date.setText(getCurrentDate());

        ordernumber = findViewById(R.id.ordernumber);
        ordernumber.setText(getOrderId());

         phno = user.getPhoneNumber().substring(3);

        TVnumber = findViewById(R.id.number);
        TVnumber.setText(phno);

        image = findViewById(R.id.image);
        if(mode.equals("paytm")){
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_if_paytm));
        }
        else if(mode.equals("credit"))
        {
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_if_payu));
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("second", name, importance);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void goToSo (View view) {
       Submit();
       }

    private void sendNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "second")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Enter the Amount "+amount+" and Proceed")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You Are Redirected To the Payments Page.Carefully Enter the Amount And proceed"))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(001, mBuilder.build());
    }
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
        finish();
    }
    public static String getOrderId(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currentDateTime = dateFormat.format(new Date());
        String id="VP_"+currentDateTime;

        return id;
    }
    public static String getCurrentDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd / MM / yyyy ",Locale.ENGLISH);
            String strDate = mdformat.format(calendar.getTime());

            return strDate;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    public static String getCurrentTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("hh : mm : ss a ",Locale.ENGLISH);
            String strDate = " "+mdformat.format(calendar.getTime());

            return strDate;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    private void Submit() {
        final ProgressDialog loading = ProgressDialog.show(this,"Redirecting...","Please don't press back button...",false,false);

        final String accountName = name;
        final String userEmail = email;
        final String userId = user.getUid();
        final String userAccount = number;
        final String userIfsc = IFSC;
        final String userAmount = amount;
        final String userFees = fee;
        final String userPayback = payback;
        final String userPhone = phno;
        final String transMode = mode;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        if(mode.equals("paytm")) {
                            goToUrl(paytmUrl);
                            sendNotification();
                        }
                        else{
                            goToUrl(payuUrl);
                            sendNotification();
                        }

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
                params.put(Configuration.KEY_ACTION,"order");
                params.put(Configuration.KEY_ID,userId);
                params.put(Configuration.KEY_ACC_NAME,accountName);
                params.put(Configuration.KEY_EMAIL,userEmail);
                params.put(Configuration.KEY_ACC_NUMBER,userAccount);
                params.put(Configuration.KEY_ACC_IFSC,userIfsc);
                params.put(Configuration.KEY_TXN_AMOUNT,userAmount);
                params.put(Configuration.KEY_FEES,userFees);
                params.put(Configuration.KEY_PAYBACK,userPayback);
                params.put(Configuration.KEY_DATE,getCurrentDate());
                params.put(Configuration.KEY_TIME,getCurrentTime());
                params.put(Configuration.KEY_ORDER_ID,getOrderId());
                params.put(Configuration.KEY_MOBILE,userPhone);
                params.put(Configuration.KEY_MODE,transMode);
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

