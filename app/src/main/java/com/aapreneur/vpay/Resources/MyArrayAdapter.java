package com.aapreneur.vpay.Resources;

/**
 * Created by anmol on 02-04-2018.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aapreneur.vpay.R;

import java.util.List;

public class MyArrayAdapter  extends ArrayAdapter < MyDataModel > {

        List < MyDataModel > modelList;
        Context context;
    private LayoutInflater mInflater;

// Constructors
public MyArrayAdapter(Context context, List < MyDataModel > objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        modelList = objects;
        }

@Override
public MyDataModel getItem(int position) {
        return modelList.get(position);
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
final ViewHolder vh;
        if (convertView == null) {
        View view = mInflater.inflate(R.layout.layout_row_view, parent, false);
        vh = ViewHolder.create((RelativeLayout) view);
        view.setTag(vh);
        } else {
        vh = (ViewHolder) convertView.getTag();
        }

        MyDataModel item = getItem(position);

        vh.textViewTransactionId.setText(item.getTransactionId());
        vh.textViewDate.setText(item.getDate());
        vh.textViewTime.setText(item.getTime());
        vh.textViewAmount.setText(item.getAmount());
        vh.textViewUTR.setText(item.getUTR());
        vh.textViewPaytmId.setText(item.getPaytmId());



        return vh.rootView;
        }



private static class ViewHolder {
    public final RelativeLayout rootView;

    public final TextView textViewTransactionId;
    public final TextView textViewDate;
    public final TextView textViewAmount;
    public final TextView textViewTime;
    public final TextView textViewUTR;
    public final TextView textViewPaytmId;


    private ViewHolder(RelativeLayout rootView, TextView textViewDate, TextView textViewTransactionId, TextView textViewUTR, TextView textViewTime,TextView textViewAmount,TextView textViewPaytmId) {
        this.rootView = rootView;
        this.textViewTransactionId = textViewTransactionId;
        this.textViewDate = textViewDate;
        this.textViewUTR = textViewUTR;
        this.textViewAmount = textViewAmount;
        this.textViewTime = textViewTime;
        this.textViewPaytmId = textViewPaytmId;

    }

    public static ViewHolder create(RelativeLayout rootView) {
        TextView textViewTransactionId = (TextView) rootView.findViewById(R.id.transactionId);
        TextView textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        TextView textViewUTR = (TextView) rootView.findViewById(R.id.UTR);
        TextView textViewTime = (TextView) rootView.findViewById(R.id.time);
        TextView textViewAmount = (TextView) rootView.findViewById(R.id.textViewAmount);
        TextView textViewPaytmId = (TextView) rootView.findViewById(R.id.paytmOrderId);

        return new ViewHolder(rootView, textViewDate, textViewTransactionId, textViewUTR,textViewTime,textViewAmount,textViewPaytmId);
    }
}
}