package c.aapreneur.vpay.Resources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import c.aapreneur.vpay.R;

/**
 * Created by anmol on 07-04-2018.
 */

public class MyArrayAdapterRef extends ArrayAdapter<MyDataModel> {

    List<MyDataModel> modelList;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public MyArrayAdapterRef(Context context, List<MyDataModel> objects) {
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

        if (item.getMode().equals("credit")) {
            vh.imageViewMode.setImageResource(R.drawable.ic_if_payu);
        } else if (item.getMode().equals("paytm")) {
            vh.imageViewMode.setImageResource(R.drawable.ic_if_paytm);
        }
        if (item.getPaytmId().equals("")) {
            vh.textViewParagraph.setText("Click here To Update Refrence Id");
        } else if (item.getPaytmId() != "") {
            vh.textViewParagraph.setText("Refrence Id- " + item.getPaytmId());
        }

        return vh.rootView;
    }


    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final TextView textViewDate;
        public final TextView textViewAmount;
        public final TextView textViewTime;
        public final TextView textViewParagraph;
        public final ImageView imageViewMode;


        private ViewHolder(RelativeLayout rootView, TextView textViewDate, TextView textViewTime, TextView textViewAmount, ImageView imageViewMode, TextView textViewParagraph) {
            this.rootView = rootView;
            this.textViewParagraph = textViewParagraph;
            this.textViewDate = textViewDate;
            this.textViewAmount = textViewAmount;
            this.textViewTime = textViewTime;
            this.imageViewMode = imageViewMode;

        }

        public static MyArrayAdapterRef.ViewHolder create(RelativeLayout rootView) {
            TextView textViewDate = rootView.findViewById(R.id.textViewDate);
            TextView textViewTime = rootView.findViewById(R.id.time);
            TextView textViewAmount = rootView.findViewById(R.id.textViewAmount);
            TextView textViewParagraph = rootView.findViewById(R.id.paragraph);
            ImageView imageViewMode = rootView.findViewById(R.id.mode);
            return new MyArrayAdapterRef.ViewHolder(rootView, textViewDate, textViewTime, textViewAmount, imageViewMode, textViewParagraph);
        }
    }
}
