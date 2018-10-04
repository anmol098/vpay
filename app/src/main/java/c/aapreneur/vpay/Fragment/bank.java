package c.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import c.aapreneur.vpay.R;
import c.aapreneur.vpay.Resources.Configuration;
import c.aapreneur.vpay.Resources.MyArrayAdapter;
import c.aapreneur.vpay.Resources.MyDataModel;
import c.aapreneur.vpay.receipt;

import static android.view.View.GONE;


public class bank extends Fragment {

    private ListView listView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private ArrayList <MyDataModel> list;
    private MyArrayAdapter adapter;


    public bank() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank, container, false);
        list = new ArrayList < > ();
        adapter = new MyArrayAdapter(getActivity(), list);
        listView = view.findViewById(R.id.listView);
        linearLayout = view.findViewById(R.id.view_empty);
        relativeLayout = view.findViewById(R.id.activity_main);
        listView.setAdapter(adapter);
        new ReadData1().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mode = list.get(position).getMode();
                String amount = list.get(position).getAmount();
                String remarks = list.get(position).getRemarks();
                String utr = list.get(position).getUTR();
                String fees = list.get(position).getFees();
                String payback = list.get(position).getPayback();
                String date = list.get(position).getDate();
                String txnID = list.get(position).getTransactionId();
                String orderid = list.get(position).getOrderId();

                Intent i = new Intent(getActivity(),receipt.class);
                i.putExtra("MODE",mode);
                i.putExtra("AMOUNT",amount);
                i.putExtra("REMARKS",remarks);
                i.putExtra("UTR",utr);
                i.putExtra("FEES",fees);
                i.putExtra("PAYBACK",payback);
                i.putExtra("DATE",date);
                i.putExtra("TXNID",txnID);
                i.putExtra("VPAYID",orderid);
                startActivity(i);
            }
        });


        return view;
    }
    class ReadData1 extends AsyncTask < Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex;
        int x;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x = list.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            relativeLayout.setVisibility(View.VISIBLE);
        }

        @Nullable
        @Override
        protected Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.recentTransaction(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                MyDataModel model = new MyDataModel();
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String paytmId = innerObject.getString("paytmId");
                                String orderId = innerObject.getString("orderId");
                                String Amount = innerObject.getString("amount");
                                String date = innerObject.getString("date");
                                String time = innerObject.getString("time");
                                String transactionId = innerObject.getString("transactionId");
                                String UTR = innerObject.getString("UTR");
                                String mode = innerObject.getString("mode");
                                String remarks = innerObject.getString("remarks");
                                String fees = innerObject.getString("fees");
                                String payback = innerObject.getString("payback");

                                model.setUTR(UTR);
                                model.setTransactionId(transactionId);
                                model.setAmount(Amount);
                                model.setDate(date);
                                model.setTime(time);
                                model.setOrderId(orderId);
                                model.setPaytmId(paytmId);
                                model.setMode(mode);
                                model.setRemarks(remarks);
                                model.setFees(fees);
                                model.setPayback(payback);

                                list.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            relativeLayout.setVisibility(GONE);
            if (list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "No data found", Toast.LENGTH_LONG).show();
                listView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
