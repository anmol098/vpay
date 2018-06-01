package com.aapreneur.vpay.Resources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aapreneur.vpay.R;

import java.util.List;

/**
 * Created by anmol on 07-04-2018.
 */

public class MyArrayAdapterRef extends ArrayAdapter< MyDataModel > {

    List< MyDataModel > modelList;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public MyArrayAdapterRef(Context context, List < MyDataModel > objects) {
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
        final MyArrayAdapterRef.ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.layout_row_view_add_ref, parent, false);
            vh = MyArrayAdapterRef.ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (MyArrayAdapterRef.ViewHolder) convertView.getTag();
        }

        MyDataModel item = getItem(position);

        vh.textViewDate.setText(item.getDate());
        vh.textViewTime.setText(item.getTime());
        vh.textViewAmount.setText(item.getAmount());

        if(item.getMode().equals("credit")){
            vh.imageViewMode.setImageResource(R.drawable.ic_if_payu);
        }
        else if(item.getMode().equals("paytm")){
            vh.imageViewMode.setImageResource(R.drawable.ic_if_paytm);
        }


        return vh.rootView;
    }



    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final TextView textViewDate;
        public final TextView textViewAmount;
        public final TextView textViewTime;
        public final ImageView imageViewMode;


        private ViewHolder(RelativeLayout rootView, TextView textViewDate,TextView textViewTime,TextView textViewAmount,ImageView imageViewMode) {
            this.rootView = rootView;

            this.textViewDate = textViewDate;
            this.textViewAmount = textViewAmount;
            this.textViewTime = textViewTime;
            this.imageViewMode = imageViewMode;

        }

        public static MyArrayAdapterRef.ViewHolder create(RelativeLayout rootView) {
            TextView textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
            TextView textViewTime = (TextView) rootView.findViewById(R.id.time);
            TextView textViewAmount = (TextView) rootView.findViewById(R.id.textViewAmount);
            ImageView imageViewMode = (ImageView) rootView.findViewById(R.id.mode);
            return new MyArrayAdapterRef.ViewHolder(rootView,textViewDate, textViewTime,textViewAmount,imageViewMode);
        }
    }
}
