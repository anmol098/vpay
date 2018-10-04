package c.aapreneur.vpay;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.aapreneur.vpay.Resources.Configuration;
import c.aapreneur.vpay.Resources.Upload;

import static c.aapreneur.vpay.checkout.getOrderId;

public class FirstSignup extends AppCompatActivity {

    String fileTextUrl = "https://img7.androidappsapk.co/300/7/3/a/com.profile.admires_stalkers_unknown.png";

    private EditText editTextEmail;
    private EditText editTextName;
    String email;
    String name;
    String id;
    String mobile;
    String referral = "null";
    boolean isPremissonGranted=false;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    //firebase objects
    private Uri filePath;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_signup);

        editTextEmail = findViewById(R.id.email);
        editTextName = findViewById(R.id.name);
        id = user.getUid();
        mobile = user.getPhoneNumber();
        TextView textRefer = findViewById(R.id.referral);
        textRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(FirstSignup.this, R.style.CustomDialogTheme);
                final EditText input = new EditText(FirstSignup.this);
                input.setHint("Referral code");
                alert.setTitle("Enter Referral Code ");
                alert.setView(input);
                LinearLayout layout = new LinearLayout(FirstSignup.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(input);

                alert.setView(layout);
                alert.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                referral = input.getText().toString().trim();


                            }
                        });
                alert.show();
            }

        });


        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Configuration.DATABASE_PATH_UPLOADS);

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = editTextEmail.getText().toString().trim();
                name = editTextName.getText().toString().trim();
                if (name.isEmpty() || name.length() < 3) {
                    editTextName.setError("at least 3 characters");
                    return;
                } else {
                    editTextName.setError(null);
                }

                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("enter a valid email address");
                    return;
                } else {
                    editTextEmail.setError(null);
                }
                updateprofile();
                Submit();
            }
        });
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
                if(isPremissonGranted) {
                    showFileChooser();
                }
            }
        });
    }
    private void requestStoragePermission() {
        Dexter.withActivity(FirstSignup.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            isPremissonGranted=true;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadFile(filePath);
        }else {
            Toast.makeText(getApplicationContext(), "No file chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(Uri data) {
        final StorageReference sRef = mStorageReference.child(Configuration.STORAGE_PATH_UPLOADS + user.getPhoneNumber() + ".jpg");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Upload upload = new Upload(getOrderId(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                        fileTextUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        Toast.makeText(getApplicationContext(), "Profile Image Updated Successfully", Toast.LENGTH_LONG).show();
                        updateprofile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Toast.makeText(getApplicationContext(), progress+"% uploaded", Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void updateprofile()
    {
        auth = FirebaseAuth.getInstance();
        if(user!=null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editTextName.getText().toString())
                    .setPhotoUri(Uri.parse(fileTextUrl))
            .build();

            user.updateProfile(profileUpdates);
        }
    }

    private void Submit() {
        final ProgressDialog loading = ProgressDialog.show(this,"Registering ...","Please wait...",false,false);

        final String userName = name;
        final String userEmail = email;
        final String userId = id;
        final String userMobile = mobile;
        final String userImage = fileTextUrl;
        final String userReferral = referral;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        editTextName.setText(null);
                        editTextEmail.setText(null);
                        startActivity(new Intent(FirstSignup.this, bank_details.class));
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Configuration.KEY_ACTION,"insert");
                params.put(Configuration.KEY_ID,userId);
                params.put(Configuration.KEY_NAME,userName);
                params.put(Configuration.KEY_EMAIL,userEmail);
                params.put(Configuration.KEY_MOBILE,userMobile);
                params.put(Configuration.KEY_IMAGE,userImage);
                params.put(Configuration.KEY_REFERRAL, userReferral);
                params.put(Configuration.KEY_RESULT,"1");
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
}