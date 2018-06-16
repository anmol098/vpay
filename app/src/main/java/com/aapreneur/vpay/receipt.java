package com.aapreneur.vpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class receipt extends AppCompatActivity {

    TextView paymentGateway,TVamount,amount_1,TVremarks,TVutr,amt,TVfees,paybackAmount,remarks_pending;
    String mode,amount,remarks,utr,fees,payback,date,txnID,orderid;
    LinearLayout payDetails;
    LinearLayout pendingPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        paymentGateway = findViewById(R.id.paymentGateway);
        TVamount = findViewById(R.id.amount);
        amount_1 = findViewById(R.id.Amount_1);
        TVremarks = findViewById(R.id.remarks);
        TVutr = findViewById(R.id.UTR);
        amt = findViewById(R.id.Amt);
        TVfees = findViewById(R.id.fees);
        paybackAmount = findViewById(R.id.paybackAmount);
        remarks_pending = findViewById(R.id.remarks_pending);
        payDetails = findViewById(R.id.paymentDetails);
        pendingPay = findViewById(R.id.pending_pay);
        ImageView close = findViewById(R.id.close);

        Intent intent = getIntent();
        mode = intent.getExtras().getString("MODE");
        amount = intent.getExtras().getString("AMOUNT");
        remarks = intent.getExtras().getString("REMARKS");
        utr = intent.getExtras().getString("UTR");
        fees = intent.getExtras().getString("FEES");
        date = intent.getExtras().getString("DATE");
        payback = intent.getExtras().getString("PAYBACK");
        txnID = intent.getExtras().getString("TXNID");
        orderid = intent.getExtras().getString("VPAYID");


        TVamount.setText(amount);
        amt.setText(amount);
        TVutr.setText(getString(R.string.utr, utr));
        TVfees.setText(getString(R.string.fees, fees));
        paybackAmount.setText(payback);

        if(mode.equals("paytm"))
            paymentGateway.setText("Paid Using Paytm Wallet");
        else if (mode.equals("credit"))
            paymentGateway.setText("Paid Using Credit Card");
        amount_1.setText(payback);


        if(remarks.equals("0")){
            remarks_pending.setText(getString(R.string.ref_not_updated));
            pendingPay.setVisibility(View.VISIBLE);
            payDetails.setVisibility(View.GONE);
        }else if (remarks.equals("1")){
            remarks_pending.setText(getString(R.string.date,date));
            pendingPay.setVisibility(View.VISIBLE);
            payDetails.setVisibility(View.GONE);
        }else if(remarks.equals("2")){
            pendingPay.setVisibility(View.GONE);
            payDetails.setVisibility(View.VISIBLE);
        }
        else if(remarks.equals("3")&&!txnID.equals(""))
        {
            remarks_pending.setText(getString(R.string.refund,txnID));
            pendingPay.setVisibility(View.VISIBLE);
            payDetails.setVisibility(View.GONE);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@theaapreneur.zohodesk.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Issue Related to order id "+orderid);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(receipt.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}