package com.aapreneur.vpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class receipt extends AppCompatActivity {

    TextView paymentGateway,TVamount,amount_1,TVremarks,TVutr,amt,TVfees,paybackAmount,remarks_pending;
    String mode,amount,remarks,utr,fees,payback,date,txnID;
    LinearLayout payDetails;
    LinearLayout pendingPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        paymentGateway = (TextView)findViewById(R.id.paymentGateway);
        TVamount = (TextView )findViewById(R.id.amount);
        amount_1 = (TextView)findViewById(R.id.Amount_1);
        TVremarks = (TextView)findViewById(R.id.remarks);
        TVutr = (TextView)findViewById(R.id.UTR);
        amt = (TextView)findViewById(R.id.Amt);
        TVfees = (TextView)findViewById(R.id.fees);
        paybackAmount = (TextView)findViewById(R.id.paybackAmount );
        remarks_pending = (TextView)findViewById(R.id.remarks_pending);
        payDetails = (LinearLayout)findViewById(R.id.paymentDetails);
        pendingPay = (LinearLayout)findViewById(R.id.pending_pay);

        Intent intent = getIntent();
        mode = intent.getExtras().getString("MODE");
        amount = intent.getExtras().getString("AMOUNT");
        remarks = intent.getExtras().getString("REMARKS");
        utr = intent.getExtras().getString("UTR");
        fees = intent.getExtras().getString("FEES");
        date = intent.getExtras().getString("DATE");
        payback = intent.getExtras().getString("PAYBACK");
        txnID = intent.getExtras().getString("TXNID");


        TVamount.setText(amount);

        if(mode.equals("paytm"))
            paymentGateway.setText("Paid Using Paytm Wallet");
        else if (mode.equals("credit"))
            paymentGateway.setText("Paid Using Credit Card");
        amount_1.setText(amount);


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
        else{
            amt.setText(amount);
            TVutr.setText(getString(R.string.utr,utr));
            TVfees.setText(getString(R.string.fees,fees));
            paybackAmount.setText(payback);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
