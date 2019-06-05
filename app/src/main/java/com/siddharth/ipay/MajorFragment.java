package com.siddharth.ipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;


public class MajorFragment extends Fragment {
    ArrayList<String> phoneBookList = new ArrayList<>();
    List<String> finalList = new ArrayList<>();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = rootRef.child("Personal");
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    View v;


    public MajorFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_major, container, false);
        getContact();
        getContactList();
        return v;
    }

    private void getContact(){
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneBookList.add(phoneNumber);
                    }
                    phones.close();
                }

            }
        }

    }

    private void getContactList() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> firebaseDatabaseList = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String firebaseDatabasePhoneNumber = ds.getKey();
                    firebaseDatabaseList.add(firebaseDatabasePhoneNumber);
                }

                for(String phoneBookNumber : phoneBookList) {
                    if (firebaseDatabaseList.contains(phoneBookNumber)) {
                        finalList.add(phoneBookNumber);
                    }
                }
                mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter = new MainAdapter(finalList);
                mRecyclerView.setAdapter(mAdapter);
                //Do what you need to do with the finalList
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

}
}
