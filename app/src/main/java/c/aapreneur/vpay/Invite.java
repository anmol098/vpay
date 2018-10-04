package c.aapreneur.vpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.hashids.Hashids;


public class Invite extends AppCompatActivity {
    String message;
    private ImageView image;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        image = findViewById(R.id.profile);
        TextView textID = findViewById(R.id.unique);
        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Long number = Long.parseLong(user.getPhoneNumber().substring(3));
        Hashids hashids = new Hashids("VpAyswrayugdfbczzxbsdalqwsergydfjfmnbhdhsjejejskiqqwrhdhsbsnxncghdjskdskjdsdsbsdkrjsdkjvbfgb", 6);
        String hash = hashids.encode(number);
        textID.setText(hash);

        message = "Register on VPay with " + hash + " and earn ₹20 or other exciting gifts. Download on \n vpay.page.link/newinstall";
        findViewById(R.id.invite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


        Glide.with(this)
                .load(R.drawable.img)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);
    }
}