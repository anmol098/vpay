package c.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import c.aapreneur.vpay.CircleTransform;
import c.aapreneur.vpay.R;

import static android.content.Context.MODE_PRIVATE;


public class profile extends Fragment{


    private TextView name;

    private ImageView image;

    private  TextView mobile;

    private TextView acoountNumber;

    private TextView IFSC;

    private TextView tvemail;
    FirebaseAuth mAuth;

    public profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        name = view.findViewById(R.id.name);
        mobile = view.findViewById(R.id.mobile);
        image = view.findViewById(R.id.profile);
        acoountNumber = view.findViewById(R.id.account);
        IFSC = view.findViewById(R.id.ifsc);
        tvemail = view.findViewById(R.id.email);


        if (user != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("pref_data", MODE_PRIVATE);
            name.setText(user.getDisplayName());
            mobile.setText(user.getPhoneNumber());
            tvemail.setText(prefs.getString("email", "defaultValue"));
            acoountNumber.setText(prefs.getString("account", "defaultValue"));
            IFSC.setText(prefs.getString("ifsc", "defaultValue"));


           Glide.with(this)
                    .load(user.getPhotoUrl())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getActivity()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        }
        return view;
    }
}
