package com.siddharth.ipay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Request extends AppCompatActivity {
    List<RequestModel> historylist = new ArrayList<RequestModel>();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Request");
    DatabaseReference mRefPre = FirebaseDatabase.getInstance().getReference().child("Personal");
    RequestAdapter adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        mRefPre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.child("Email").getValue().equals(mAuth.getCurrentUser().getEmail())) {
                            ;
                            mRef = mRef.child(d.child("Phone").getValue().toString());
                            mRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                                            RequestModel his = new RequestModel(d.child("Sender").getValue().toString(), d.child("Amount").getValue().toString(), d.child("Name").getValue().toString());
                                            historylist.add(his);
                                        }
                                    }
                                    RecyclerView recyclerView = findViewById(R.id.history_recycler);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    adapter = new RequestAdapter(getApplicationContext(), historylist);
                                    recyclerView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
