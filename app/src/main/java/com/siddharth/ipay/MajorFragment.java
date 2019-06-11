package com.siddharth.ipay;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class MajorFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 200;
    View v;
    DatabaseReference myRef;
    List<String> firebaselist = new ArrayList<String>();
    List<String> phonelist = new ArrayList<String>();
    List<String> namelist = new ArrayList<String>();
    List<Contact> finallist = new ArrayList<Contact>();
    MyRecyclerViewAdapter adapter;
    int t = 0;
    ProgressDialog progressBar;
    private Handler mainHandler = new Handler();
    private Context context;

    public MajorFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getContext();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_major, container, false);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        progressBar = new ProgressDialog(context);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage("Loading....");
        progressBar.show();
        FirebaseList firebasethread = new FirebaseList();
        new Thread(firebasethread).start();
        ContactList contactthread = new ContactList();
        new Thread(contactthread).start();
        return v;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    public void checker() {
        if (t >= 2) {
            FinalList finalthread = new FinalList();
            new Thread(finalthread).start();
        }
    }

    class FirebaseList implements Runnable {
        @Override
        public void run() {
            if (checkPermission()) {
                myRef = FirebaseDatabase.getInstance().getReference().child("Personal");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                firebaselist.add(d.getKey());
                            }
                        }
                        t = ++t;
                        checker();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                requestPermission();
            }
        }
    }

    class ContactList implements Runnable {
        @Override
        public void run() {
            ContentResolver cr = getContext().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            namelist.add(name);
                            phonelist.add(phoneNo);
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
            t = ++t;
            checker();
        }
    }

    class FinalList implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < phonelist.size(); i++) {
                for (int j = 0; j < firebaselist.size(); j++) {
                    if (phonelist.get(i).equals(firebaselist.get(j))) {
                        Contact finall = new Contact(namelist.get(i), phonelist.get(i));
                        finallist.add(finall);
                    }
                }
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new MyRecyclerViewAdapter(getContext(), finallist);
                    recyclerView.setAdapter(adapter);
                    progressBar.dismiss();
                }
            });
        }
    }
}
