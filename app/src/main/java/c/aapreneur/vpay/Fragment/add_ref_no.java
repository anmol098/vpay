package c.aapreneur.vpay.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
import c.aapreneur.vpay.Resources.MyArrayAdapterRef;
import c.aapreneur.vpay.Resources.MyDataModel;

import static android.view.View.GONE;


public class add_ref_no extends Fragment {
    public int x;

    private ListView listView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private ArrayList <MyDataModel> list;
    private MyArrayAdapterRef adapter;
    private String orderId;
    private String paytmId;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

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
        listView = view.findViewById(R.id.listView);
        linearLayout = view.findViewById(R.id.view_empty);
        relativeLayout = view.findViewById(R.id.activity_main);
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
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.CustomDialogTheme);
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

                                String orderId = innerObject.getString("orderId");
                                String Amount = innerObject.getString("amount");
                                String date = innerObject.getString("date");
                                String time = innerObject.getString("time");
                                String paytmId = innerObject.getString("paytmId");
                                String mode = innerObject.getString("mode");

                                model.setAmount(Amount);
                                model.setDate(date);
                                model.setTime(time);
                                model.setOrderId(orderId);
                                model.setPaytmId(paytmId);
                                model.setMode(mode);

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
                listView.setVisibility(GONE);
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
            String name = user.getDisplayName();
            JSONObject jsonObject = Configuration.updateRef(orderId, paytmId,name);
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