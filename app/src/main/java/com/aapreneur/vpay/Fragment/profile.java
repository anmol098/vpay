package com.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aapreneur.vpay.CircleTransform;
import com.aapreneur.vpay.Resources.Configuration;
import com.aapreneur.vpay.checkout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.aapreneur.vpay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class profile extends Fragment{


    private TextView name;

    private ImageView image;

    private  TextView mobile;

    private TextView acoountNumber;

    private TextView IFSC;

    private TextView tvemail;


    FirebaseAuth mAuth;

    public profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        name =(TextView)view.findViewById(R.id.name);
        mobile = (TextView)view.findViewById(R.id.mobile);
        image=(ImageView)view.findViewById(R.id.profile);
        acoountNumber=(TextView)view.findViewById(R.id.account);
        IFSC = (TextView)view.findViewById(R.id.ifsc);
        tvemail = (TextView)view.findViewById(R.id.email);


        if (user != null) {
            name.setText(user.getDisplayName());
            mobile.setText(user.getPhoneNumber());
            new ReadAccount().execute();
            new ReadProfile().execute();


           Glide.with(this)
                    .load(user.getPhotoUrl())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getActivity()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        }
        return view;
    }
    class ReadAccount extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String name,number,ifsc;
        FirebaseAuth mAuth;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Nullable
        @Override
        public Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.readAccount(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                name = innerObject.getString("name");

                                number = innerObject.getString("number");

                                ifsc = innerObject.getString("ifsc");

                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            IFSC.setText(ifsc);
            acoountNumber.setText(number);
        }
    }

    class ReadProfile extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String name,email;
        FirebaseAuth mAuth;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Please Wait...");
            dialog.setMessage("Getting Your Profile");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }



        @Nullable
        @Override
        public Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.readProfile(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                name = innerObject.getString("name");

                                email = innerObject.getString("email");

                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvemail.setText(email);
            dialog.dismiss();

        }
    }

}
