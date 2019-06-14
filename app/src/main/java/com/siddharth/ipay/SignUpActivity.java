package com.siddharth.ipay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class SignUpActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;

    private Button mSelectImage;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mPassword;
    private EditText mPasswordcheck;
    StorageReference filepath;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    Personal person = new Personal();
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_INTENT = 2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Personal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mSelectImage = findViewById(R.id.profileimage_button_signup);
        mEmail = findViewById(R.id.email_box_signup);
        mUsername = findViewById(R.id.username_box_signup);
        mPhone = findViewById(R.id.phone_box_signup);
        mPassword = findViewById(R.id.password_box_signup);
        mPasswordcheck = findViewById(R.id.check_password_box_signup);

        mProgressDialog = new ProgressDialog(this);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
                } else {
                    requestPermission();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            Uri uri = data.getData();
            filepath = mStorageRef.child(mEmail.getText().toString()).child("Profile Photo");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Couldn't upload the profile photo", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public void onSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }


    public void onSubmit(View view) {
        final String email = mEmail.getText().toString().trim();
        final String password  = mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage("Registering Please Wait...");
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if ((mPassword.getText().toString()).equals(mPasswordcheck.getText().toString())) {
                                person.setEmail(email);
                                person.setPassword(password);
                                person.setPhone(mPhone.getText().toString());
                                person.setUsername(mUsername.getText().toString());
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(person.getUsername())) {
                                            Toast.makeText(getApplicationContext(), "Username exists", Toast.LENGTH_LONG).show();
                                        }else{
                                            DatabaseReference myRefchild = myRef.child(person.getPhone());
                                            myRefchild.child("Phone").setValue(person.getPhone());
                                            myRefchild.child("Email").setValue(person.getEmail());
                                            myRefchild.child("Password").setValue(person.getPassword());
                                            myRefchild.child("Username").setValue(person.getUsername());
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(), "Passwords dont match", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }
}
