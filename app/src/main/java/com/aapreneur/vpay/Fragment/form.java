package com.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.aapreneur.vpay.R;
import com.aapreneur.vpay.Resources.Configuration;
import com.aapreneur.vpay.bank_details;
import com.aapreneur.vpay.checkout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class form extends Fragment{

    public String fee;
    public EditText amount;
    public double AmtPayback,fees,txnAmount;
    public Button buttonProceed;
    public double percent ;
    TextInputLayout til;

    Locale INR = new Locale("en", "IN");
    NumberFormat inrFormat = NumberFormat.getCurrencyInstance(INR);


    public form() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        amount = (EditText) view.findViewById(R.id.amount_1);
        buttonProceed = (Button) view.findViewById(R.id.proceed);
        til = (TextInputLayout) view.findViewById(R.id.amount);

        buttonProceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (amount.getText().toString().equals("")) {
                    til.setError("Please enter a valid amount between ₹100 and ₹2000");
                }else if(Integer.parseInt(amount.getText().toString())< 100){
                til.setError("Please enter a valid amount between ₹100 and ₹2000");
            }else if (Integer.parseInt(amount.getText().toString())> 2000){
                    til.setError("Please enter a valid amount between ₹100 and ₹2000");
                }
                else {
                    til.setError(null);
                    if (isInternetOn()) {

                        new ReadData1().execute();
                        Snackbar snackbar = Snackbar
                                .make(view, "By clicking Ok You Accept to our Terms and Services", Snackbar.LENGTH_SHORT);
                        /*.setAction("", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });*/
                        snackbar.show();
                    } else {
                        Toast.makeText(getActivity(), " Not Connected ", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        return view;
    }
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(getActivity(), " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
    class ReadData1 extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        int x;
        FirebaseAuth mAuth;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Calculating Your Amount...");
            dialog.setMessage("Please Wait");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Nullable
        @Override
        public Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.readData(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                fee = innerObject.getString("fee");
                                Log.i("myApp","message"+fee );
                            }
                        }
                    }
                } else {

                    fee="invalid";

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            final String amt = amount.getText().toString().trim();

                if (fee.equals("invalid")) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Aw, Snap!!")
                            .setMessage("Servers Unreachable Try After Sometime")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                            .setCancelable(false);


                } else {
                    try {
                        txnAmount = Integer.parseInt(amt);
                        percent = Double.parseDouble(fee);

                    } catch (NumberFormatException e) {
                        //
                    }
                    fees = txnAmount * percent;
                    AmtPayback = txnAmount - fees;
                    Toast.makeText(getContext(), "Your Transaction fees is " + (percent * 100) + "%", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Transfer " + inrFormat.format(AmtPayback))
                            .setMessage("You Will Be Getting " + inrFormat.format(AmtPayback) + " In Your Bank Account")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new ReadAccount().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                            .setCancelable(false);
            }
        }
    }

    class ReadAccount extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String name,number,ifsc;
        FirebaseAuth mAuth;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Fetching Your Account Details");
            dialog.setMessage("Please Wait");
            dialog.show();
            dialog.setCancelable(false);
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
                                Log.i("myApp","message"+name );
                                number = innerObject.getString("number");
                                Log.i("myApp","message"+number );
                                ifsc = innerObject.getString("ifsc");
                                Log.i("myApp","message"+ifsc );
                            }
                        }
                    }
                } else {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Aw, Snap!!")
                            .setMessage("Servers Unreachable")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                    .setCancelable(false);


                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
           if(name.equals("null")){

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Account Not Found")
                        .setMessage("It Seems Like No Bank Account Is Linked To your profile")
                        .setPositiveButton("Link Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getActivity(),bank_details.class);
                                startActivity(i);



                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                .setCancelable(false);

            }
            else {
                Intent intent = new Intent(getActivity(), checkout.class);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                intent.putExtra("ifsc", ifsc);
                intent.putExtra("payback", AmtPayback);
                intent.putExtra("amount", txnAmount);
                intent.putExtra("fees", fees);
                startActivity(intent);
            }
        }
    }
}
