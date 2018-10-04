package c.aapreneur.vpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import c.aapreneur.vpay.Resources.Configuration;

public class bank_details extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextAccountNumber;
    private EditText editTextIFSC;
    public Button buttonProceed, buttonSkip;
    String id;
    private TextInputLayout tilName,tilNumber,tilIFSC;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    SharedPreferences.Editor editor = getSharedPreferences("pref_data", MODE_PRIVATE).edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextName = findViewById(R.id.name);
        editTextAccountNumber = findViewById(R.id.account_number_1);
        editTextIFSC = findViewById(R.id.IFSC_1);
        tilName = findViewById(R.id.name1);
        tilNumber = findViewById(R.id.account_number);
        tilIFSC = findViewById(R.id.IFSC);
        buttonProceed = findViewById(R.id.proceed);
        buttonSkip = findViewById(R.id.skip);
        ImageView close = findViewById(R.id.close);


        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextName.getText().toString().equals(""))
                    tilName.setError("Please Enter the Account Holder Name");
                else if(editTextAccountNumber.getText().toString().equals(""))
                    tilNumber.setError("Please enter the Account Number");
                else if(editTextIFSC.getText().toString().equals(""))
                    tilIFSC.setError("Please Enter the IFSC code");
                else {
                    tilIFSC.setError(null);
                    tilNumber.setError(null);
                    tilName.setError(null);
                    submit();
                }
            }
        });
        SharedPreferences prefs = getSharedPreferences("pref_data", MODE_PRIVATE);
        String skip = prefs.getString("skip", "0");
        if (skip.equals("1")) {
            buttonSkip.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("skip", "1");
                editor.apply();
                startActivity(new Intent(bank_details.this, Main2Activity.class));
                finish();
            }
        });
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("skip", "1");
                editor.apply();
                startActivity(new Intent(bank_details.this, Main2Activity.class));
                finish();
            }
        });
    }

    private void submit() {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        final String accountName = editTextName.getText().toString().trim();
        final String accountNumber = editTextAccountNumber.getText().toString().trim();
        final String accountIFSC = editTextIFSC.getText().toString().trim().toUpperCase();
        id = user.getUid();


        new UpdateDataActivity().execute();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "Sucess", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(FirstSignup.this, MainActivity.class));
                        //finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Configuration.KEY_ACTION, "insertAccount");
                params.put(Configuration.KEY_ID, id);
                params.put(Configuration.KEY_ACC_NAME, accountName);
                params.put(Configuration.KEY_ACC_NUMBER, accountNumber);
                params.put(Configuration.KEY_ACC_IFSC, accountIFSC);
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

    class UpdateDataActivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        String result = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(bank_details.this);
            dialog.setTitle("Hey Wait Please.....");
            dialog.setMessage("Setting UP Your Account");
            dialog.show();

        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = Configuration.updateData(id, "1");
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
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            startActivity(new Intent(bank_details.this, Main2Activity.class));
            finish();
        }

    }
}
