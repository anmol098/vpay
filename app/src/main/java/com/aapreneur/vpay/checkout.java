package com.aapreneur.vpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class checkout extends AppCompatActivity {

    TextView account,ifsc;
    TextView AmtPayback,fees,txnAmount;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    Locale INR = new Locale("en", "IN");
    NumberFormat inrFormat = NumberFormat.getCurrencyInstance(INR);

    String name,number,IFSC,amount,fee,payback,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = "anmol@test.com";
        number = intent.getStringExtra("number");
        IFSC = intent.getStringExtra("ifsc");
        amount = String.valueOf(inrFormat.format(intent.getDoubleExtra("amount",0)));
        fee = String.valueOf(inrFormat.format(intent.getDoubleExtra("fees",0)));
        payback = String.valueOf(inrFormat.format(intent.getDoubleExtra("payback",0)));



        account = (TextView)findViewById(R.id.bank);
        account.setText(number);

        ifsc = (TextView)findViewById(R.id.ifsc);
        ifsc.setText(IFSC);

        txnAmount = (TextView)findViewById(R.id.amount);
        txnAmount.setText(amount);

        fees = (TextView)findViewById(R.id.fees);
        fees.setText(fee);

        AmtPayback = (TextView)findViewById(R.id.payback);
        AmtPayback.setText(payback);
    }
    public void goToSo (View view) {
        Submit();

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

        final String userName = name;
        final String userEmail = email;
        final String userId = user.getUid();
        final String userAccount = number;
        final String userIfsc = IFSC;
        final String userAmount = amount;
        final String userFees = fee;
        final String userPayback = payback;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        goToUrl ( "http://p-y.tm/P46L4T3lX");

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
                params.put(Configuration.KEY_ACC_NAME,userName);
                params.put(Configuration.KEY_EMAIL,userEmail);
                params.put(Configuration.KEY_ACC_NUMBER,userAccount);
                params.put(Configuration.KEY_ACC_IFSC,userIfsc);
                params.put(Configuration.KEY_TXN_AMOUNT,userAmount);
                params.put(Configuration.KEY_FEES,userFees);
                params.put(Configuration.KEY_PAYBACK,userPayback);
                params.put(Configuration.KEY_DATE,getCurrentDate());
                params.put(Configuration.KEY_TIME,getCurrentTime());
                params.put(Configuration.KEY_ORDER_ID,getOrderId());
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

