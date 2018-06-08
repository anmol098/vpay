package com.aapreneur.vpay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.net.Uri;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aapreneur.vpay.Fragment.add_ref_no;
import com.aapreneur.vpay.Fragment.bank;
import com.aapreneur.vpay.Fragment.form;
import com.aapreneur.vpay.Fragment.profile;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.aapreneur.vpay.app.utils.CrossfadeWrapper;
import com.aapreneur.vpay.app.utils.SystemUtils;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Main2Activity extends AppCompatActivity {

    String email,account,ifsc;



    private static final int PROFILE_SETTING = 1;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private MiniDrawer miniResult = null;
    private Crossfader crossFader;
    private ProfileDrawerItem mAccountProfile;
    private FragmentManager mFragmentManager = getSupportFragmentManager();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkFirstRun();


        String number = user.getPhoneNumber();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
        }*/

        if(number!= null)
            FirebaseMessaging.getInstance().subscribeToTopic(number.substring(1));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("TAG", "Key: " + key + " Value: " + value);
            }
        }



        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(user.getPhotoUrl()).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });







        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem()
                .withName(user.getDisplayName())
                .withEmail(user.getPhoneNumber())
                .withIcon(user.getPhotoUrl());



        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(false)
                .addProfiles(
                        profile,
                        new ProfileSettingDrawerItem()
                                .withName("Account Setting")
                                .withDescription("Manage Your Account")
                                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings)
                                        .actionBar()
                                        .paddingDp(5)
                                        .colorRes(R.color.material_drawer_primary_text))
                                .withIdentifier(PROFILE_SETTING)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        /*if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withEmail("batman@gmail.com").withIcon(getResources().getDrawable(R.drawable.arrow));
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }*/
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult)  //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("PAY")
                                .withIcon(GoogleMaterial.Icon.gmd_input)
                                .withIdentifier(1)
                                .withTag(form.class.getName()),
                        new PrimaryDrawerItem()
                                .withName("Add Ref No.")
                                .withIcon(GoogleMaterial.Icon.gmd_receipt).withBadge("100")
                                .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorPrimary))
                                .withIdentifier(2)
                                .withTag(add_ref_no.class.getName()),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_multi_drawer)
                                .withIcon(FontAwesome.Icon.faw_rupee_sign)
                                .withIdentifier(3)
                                .withTag(bank.class.getName()),
                        new PrimaryDrawerItem()
                                .withName("Profile")
                                .withIcon(FontAwesome.Icon.faw_user)
                                .withIdentifier(4)
                                .withTag(profile.class.getName()),
                        new SectionDrawerItem()
                                .withName("SUPPORT & FEEDBACK"),
                        new SecondaryDrawerItem()
                                .withName("Help")
                                .withIcon(FontAwesome.Icon.faw_question_circle)
                                .withTag("help")
                                .withIdentifier(5),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_contact)
                                .withIcon(GoogleMaterial.Icon.gmd_business_center)
                                .withTag("contact")
                                .withIdentifier(6),
                        new SectionDrawerItem()
                                .withName("ABOUT"),
                        new SecondaryDrawerItem()
                                .withName("FAQs")
                                .withIcon(GoogleMaterial.Icon.gmd_question_answer)
                                .withTag("faq")
                                .withIdentifier(7),
                        new SecondaryDrawerItem()
                                .withName("Privacy Policy")
                                .withIcon(GoogleMaterial.Icon.gmd_lock_outline)
                                .withTag("Privacy Policy")
                                .withIdentifier(8),
                        new SecondaryDrawerItem()
                                .withName("Open Source")
                                .withIcon(FontAwesome.Icon.faw_github)
                                .withTag("open source")
                                .withIdentifier(9)

                      /* new DividerDrawerItem(),
                        new SwitchDrawerItem()
                                .withName("Switch").withIcon(Octicons.Icon.oct_tools)
                                .withChecked(true)
                                .withOnCheckedChangeListener(onCheckedChangeListener),
                        new ToggleDrawerItem()
                                .withName("Toggle")
                                .withIcon(Octicons.Icon.oct_tools)
                                .withChecked(true)
                                .withOnCheckedChangeListener(onCheckedChangeListener)*/
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof PrimaryDrawerItem) {
                            switchMenu((PrimaryDrawerItem) drawerItem);
                        }
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 8) {
                                intent = new Intent(Main2Activity.this, privacy.class);
                            }
                        else if (drawerItem.getIdentifier() == 9) {
                                new LibsBuilder()
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .withActivityTitle("Open Source Credit")
                                        .withAboutIconShown(true)
                                        .withAboutAppName(getString(R.string.app_name))
                                        .withAboutVersionShown(true)
                                        .withAboutDescription(getString(R.string.app_desc))
                                        .withLicenseShown(true)
                                        .start(Main2Activity.this);
                            } else if (drawerItem.getIdentifier() == 7) {
                                intent = new Intent(Main2Activity.this, faq.class);
                            }/* else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(Main2Activity.this, PhoneActivity.class);
                            }else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(DrawerActivity.this, AdvancedActivity.class);
                            } else if (drawerItem.getIdentifier() == 7) {
                                intent = new Intent(DrawerActivity.this, EmbeddedDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 8) {
                                intent = new Intent(DrawerActivity.this, FullscreenDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 9) {
                                intent = new Intent(DrawerActivity.this, CustomContainerActivity.class);
                            } else if (drawerItem.getIdentifier() == 10) {
                                intent = new Intent(DrawerActivity.this, MenuDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                intent = new Intent(DrawerActivity.this, MiniDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                intent = new Intent(DrawerActivity.this, FragmentActivity.class);
                            } else if (drawerItem.getIdentifier() == 13) {
                                intent = new Intent(DrawerActivity.this, CollapsingToolbarActivity.class);
                            } else if (drawerItem.getIdentifier() == 14) {
                                intent = new Intent(DrawerActivity.this, PersistentDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 15) {
                                intent = new Intent(DrawerActivity.this, CrossfadeDrawerLayoutActvitiy.class);
                            } else if (drawerItem.getIdentifier() == 20) {
                                intent = new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .intent(DrawerActivity.this);
                            }*/
                            if (intent != null) {
                                Main2Activity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withGenerateMiniDrawer(true)
                .withShowDrawerOnFirstLaunch(true)
                .withSavedInstance(savedInstanceState)
                .buildView();

        miniResult = result.getMiniDrawer();
        int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

        crossFader = new Crossfader()
                .withContent(findViewById(R.id.layout))
                .withFirst(result.getSlider(), firstWidth)
                .withSecond(miniResult.build(this), secondWidth)
                .withSavedInstance(savedInstanceState)
                .build();

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new CrossfadeWrapper(crossFader));

        //define a shadow (this is only for normal LTR layouts if you have a RTL app you need to define the other one
        crossFader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        result.setSelection(1, true);
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            new ReadProfile().execute();

            new ReadAccount().execute();

            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {

            new ReadProfile().execute();

            new ReadAccount().execute();

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }



    private Fragment mCurrentFragment;
    private void switchMenu(PrimaryDrawerItem item) {
        String tag = item.getName().getText(this);

        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        if (fragment != null) {
            if (fragment == mCurrentFragment) return;

            mFragmentManager.beginTransaction().show(fragment).detach(fragment).attach(fragment).commit();
        }
        else {
            fragment = Fragment.instantiate(this, (String) item.getTag());
            mFragmentManager.beginTransaction().add(R.id.content_frame, fragment, tag).commit();
        }

        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commit();
        }

        mCurrentFragment = fragment;
        setTitle(tag);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        //add the values which need to be saved from the crossFader to the bundle
        outState = crossFader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (SystemUtils.getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            inflater.inflate(R.menu.menu_main, menu);
            //menu.findItem(R.id.menu_1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sort).color(Color.WHITE).actionBar());
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (crossFader != null && crossFader.isCrossFaded()) {
            crossFader.crossFade();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            /*case R.id.menu_1:
                crossFader.crossFade();
                return true;*/
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, PhoneNumberAuthentication.class));
                return true;
            /*case R.id.refresh:
                onBackPressed();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ReadProfile extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex=0;
        String name;
        FirebaseAuth mAuth;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Nullable
        @Override
        public Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = com.aapreneur.vpay.Resources.Configuration.readProfile(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                name = innerObject.getString("name");

                                email = innerObject.getString("email");

                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences.Editor editor = getSharedPreferences("pref_data", MODE_PRIVATE).edit();
            editor.putString("email", email);
            editor.apply();
        }
    }
    class ReadAccount extends AsyncTask< Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex = 0;
        String name, number, ifsc;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Nullable
        @Override
        public Void doInBackground(Void... params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = com.aapreneur.vpay.Resources.Configuration.readAccount(user.getUid());
            try {

                if (jsonArray != null) {

                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String id = innerObject.getString("id");
                                name = innerObject.getString("name");

                                number = innerObject.getString("number");

                                ifsc = innerObject.getString("ifsc");

                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences.Editor editor = getSharedPreferences("pref_data", MODE_PRIVATE).edit();
            editor.putString("account", number);
            editor.putString("ifsc", ifsc);
            editor.apply();
        }
    }
}