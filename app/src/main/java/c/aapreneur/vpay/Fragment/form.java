package c.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import c.aapreneur.vpay.BuildConfig;
import c.aapreneur.vpay.R;
import c.aapreneur.vpay.Resources.Configuration;
import c.aapreneur.vpay.bank_details;
import c.aapreneur.vpay.checkout;

import static android.content.Context.MODE_PRIVATE;

public class form extends Fragment{

    public String fee,creditfee,mode;
    boolean isPromoApplied=false;
    public EditText amount;
    public double AmtPayback,fees,txnAmount;
    public Button buttonProceed;
    public double percent ;
    TextInputLayout til;
    String upper_limit="2000",promo_code,txnNum,promo_upper_limit;
    CheckBox paytmCheck,creditCheck;
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

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

        amount = view.findViewById(R.id.amount_1);
        buttonProceed = view.findViewById(R.id.proceed);
        til = view.findViewById(R.id.amount);
        paytmCheck = view.findViewById(R.id.paytmCheck);
        creditCheck = view.findViewById(R.id.creditCheck);

        paytmCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditCheck.setChecked(false);
            }
        });
        creditCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytmCheck.setChecked(false);
                creditCheck.setChecked(false);
                Toast.makeText(getActivity(), "This feature is currently not available. Please check back later.", Toast.LENGTH_LONG).show();
            }
        });

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled())
            cacheExpiration = 0;

        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activateFetched();

                    upper_limit = mFirebaseRemoteConfig.getString("upper_limit");
                    promo_code = mFirebaseRemoteConfig.getString("promo_code");
                    txnNum = mFirebaseRemoteConfig.getString("txnNum");
                    promo_upper_limit = mFirebaseRemoteConfig.getString("promo_upper_limit");
                } else {

                }

            }
        });





        buttonProceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (amount.getText().toString().equals("")) {
                    til.setError("Please enter a valid amount between ₹100 and ₹"+upper_limit);
                }else if(Integer.parseInt(amount.getText().toString())< 100){
                    til.setError("Please enter a valid amount between ₹100 and ₹" + upper_limit);
            }else if (Integer.parseInt(amount.getText().toString())> Integer.parseInt(upper_limit)){
                    til.setError("Please enter a valid amount between ₹100 and ₹"+upper_limit);
                }
                else {
                    til.setError(null);
                    if (isInternetOn()) {
                        if (paytmCheck.isChecked()) {

                            new ReadData1().execute();
                            new ReadCount().execute();
                            Snackbar snackbar = Snackbar
                                    .make(view, "By clicking Ok You Accept to our Terms of Services", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Please Select any payment method", Toast.LENGTH_LONG).show();

                        }
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
                                creditfee = innerObject.getString("creditfee");

                            }
                        }
                    }
                } else {

                    fee="invalid";

                }
            } catch (JSONException je) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
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
                   calculate();
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
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
                            .setNeutralButton("Promo Code", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.CustomDialogTheme);
                                    final EditText input = new EditText(getActivity());
                                    input.setHint("Enter Promo Code");
                                    alert.setTitle("Promo Code");
                                    alert.setView(input);
                                    LinearLayout layout = new LinearLayout(getActivity());
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.addView(input);

                                    alert.setView(layout);
                                    alert.setPositiveButton("Proceed",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    SharedPreferences prefs = getActivity().getSharedPreferences("pref_data", MODE_PRIVATE);
                                                    String count = prefs.getString("total", "0");
                                                    int promo_amt = Integer.parseInt(amount.getText().toString().trim());
                                                    if (input.getText().toString().trim().equalsIgnoreCase(promo_code)&&count.equals(txnNum)&&promo_amt<=Integer.parseInt(promo_upper_limit)) {
                                                        fee="0";
                                                        isPromoApplied=true;
                                                        calculate();
                                                        new ReadAccount().execute();

                                                    }else if(promo_amt>Integer.parseInt(promo_upper_limit)){
                                                        Toast.makeText(getContext(),"Sorry Maximum Transaction amount Should be less then ₹"+promo_upper_limit,Toast.LENGTH_LONG).show();
                                                    } else if(!input.getText().toString().trim().equalsIgnoreCase(promo_code)){
                                                        Toast.makeText(getActivity(), "Invalid Promo Code or Promo Code Not Active", Toast.LENGTH_LONG).show();

                                                    } else if (!count.equals(txnNum)) {
                                                        Toast.makeText(getActivity(), "Sorry this offer is for new users only!!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                    alert.show();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                            .setCancelable(false);
            }
        }
    }

    class ReadCount extends AsyncTask<Void, Void, Void> {
        int jIndex = 0;
        String count;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Nullable
        @Override
        public Void doInBackground(Void... params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = c.aapreneur.vpay.Resources.Configuration.count(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                count = innerObject.getString("count");
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
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("pref_data", MODE_PRIVATE).edit();
            editor.putString("total", count);
            editor.apply();
        }
    }

    private void calculate(){
        final String amt = amount.getText().toString().trim();
        if(creditCheck.isChecked())
        {
            fee = creditfee;
            paytmCheck.setChecked(false);
            mode = "credit";
        }
        else if(paytmCheck.isChecked())
        {
            creditCheck.setChecked(false);
            mode = "paytm";
        }
        try {
            txnAmount = Integer.parseInt(amt);
            percent = Double.parseDouble(fee);

        } catch (NumberFormatException e) {
            //
        }
        fees = txnAmount * percent;
        AmtPayback = txnAmount - fees;
        if(fee.equalsIgnoreCase("0")&&isPromoApplied) {
            isPromoApplied=false;
            Toast.makeText(getContext(), "Promo Code Applied Your Transaction Fees For this Transaction is " + (percent * 100) + "%", Toast.LENGTH_LONG).show();
        } else if(mode.equals("credit")&&isPromoApplied){
            Toast.makeText(getContext(),"Sorry Promo Code Not Applicable For This Transaction",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Your Transaction Fees For This Transaction is " + (percent * 100) + "%", Toast.LENGTH_LONG).show();
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
                                number = innerObject.getString("number");
                                ifsc = innerObject.getString("ifsc");
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
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
           if(name.equals("null")||number.equals("")||ifsc.equals("")){

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
                intent.putExtra("mode",mode);
                startActivity(intent);
            }
        }
    }
}
