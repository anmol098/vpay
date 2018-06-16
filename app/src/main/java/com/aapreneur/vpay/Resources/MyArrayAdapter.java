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
        vh.textViewDate.setText(item.getDate());
        vh.textViewAmount.setText(item.getAmount());
        String date = item.getDate();

        if(item.getRemarks().equals("0")){
            vh.textViewRemark.setText(R.string.ref_not_updated);

        }
        else if(item.getRemarks().equals("1")){
            vh.textViewRemark.setText("Transaction Initiated on "+date);
        }
        else if(item.getRemarks().equals("2")){
            vh.textViewRemark.setText("Transaction Succesful");
        }
        else if(item.getRemarks().equals("3")){
            vh.textViewRemark.setText("Refund Initiated");
        }



        return vh.rootView;
        }



private static class ViewHolder {
    public final RelativeLayout rootView;
    public final TextView textViewDate;
    public final TextView textViewAmount;
    public final TextView textViewRemark;


    private ViewHolder(RelativeLayout rootView, TextView textViewDate, TextView textViewAmount,TextView textViewRemark) {
        this.rootView = rootView;
        this.textViewDate = textViewDate;
        this.textViewRemark = textViewRemark;
        this.textViewAmount = textViewAmount;

    }

    public static ViewHolder create(RelativeLayout rootView) {
        TextView textViewDate = rootView.findViewById(R.id.date);
        TextView textViewAmount = rootView.findViewById(R.id.amount);
        TextView textViewRemark = rootView.findViewById(R.id.paragraph);

        return new ViewHolder(rootView, textViewDate,textViewAmount,textViewRemark);
    }
}
}