package com.aapreneur.vpay.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aapreneur.vpay.R;
import com.aapreneur.vpay.Resources.Configuration;
import com.aapreneur.vpay.Resources.MyArrayAdapter;
import com.aapreneur.vpay.Resources.MyArrayAdapterRef;
import com.aapreneur.vpay.Resources.MyDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class add_ref_no extends Fragment {
    public int x;

    private ListView listView;
    private LinearLayout linearLayout;
    private ArrayList <MyDataModel> list;
    private MyArrayAdapterRef adapter;
    private String orderId;
    private String paytmId;

    public add_ref_no() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addref, container, false);
        list = new ArrayList< >();
        adapter = new MyArrayAdapterRef(getActivity(), list);
        listView = (ListView) view.findViewById(R.id.listView);
        linearLayout = (LinearLayout)view.findViewById(R.id.view_empty);
        listView.setAdapter(adapter);
        new ReadData1().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
           public void onItemClick (AdapterView<?> parent,
                              View view,
                              int position,
                              long id) {
                orderId = list.get(position).getOrderId();
                final String test = list.get(position).getPaytmId();

                    Log.i("myApp", "txnAmount" + orderId);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    final EditText input = new EditText(getActivity());
                    input.setHint("Add Paytm Order Id");
                    alert.setTitle("Your VPay Order ID is " + orderId);
                    alert.setView(input);
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(input);

                    alert.setView(layout);
                    alert.setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (test.equals("")) {
                                        paytmId = input.getText().toString().trim();
                                        new UpdateDataActivity().execute();
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "Paytm Order Id Already updated.\n To Update Order Id Please Contact Our Costumer Care", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                    alert.show();
                }

        });
        return view;
    }
    public class ReadData1 extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x = list.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Hey Please Wait...");
            dialog.setMessage("Fetching all Your Recent Transactions");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.recentTransaction(user.getUid());
            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonArray != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                MyDataModel model = new MyDataModel();
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String orderId = innerObject.getString("orderId");
                                String Amount = innerObject.getString("amount");
                                String date = innerObject.getString("date");
                                String time = innerObject.getString("time");
                                String paytmId = innerObject.getString("paytmId");


                                model.setAmount(Amount);
                                model.setDate(date);
                                model.setTime(time);
                                model.setOrderId(orderId);
                                model.setPaytmId(paytmId);
                                list.add(model);
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
            dialog.dismiss();
            if (list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "No data found", Toast.LENGTH_LONG).show();
                listView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    class UpdateDataActivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        String result = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = Configuration.updateRef(orderId, paytmId);
            Log.i(Configuration.TAG, "Json obj ");

            try {
                if (jsonObject != null) {

                    result = jsonObject.getString("result");

                }
            } catch (JSONException je) {
                Log.i(Configuration.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }

    }

}
