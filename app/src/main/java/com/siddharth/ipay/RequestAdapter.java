package com.siddharth.ipay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Payment");
    DatabaseReference myRefReq = database.getReference().child("Request");
    DatabaseReference mRefPre = database.getReference().child("Personal");
    private List<RequestModel> mData;
    private LayoutInflater mInflater;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // data is passed into the constructor
    RequestAdapter(Context context, List<RequestModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.request_list, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder viewHolder, int i) {
        final RequestModel movie = mData.get(i);
        viewHolder.title.setText(movie.getEmail());
        viewHolder.genre.setText(movie.getAmount());
        viewHolder.btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRefChild = myRef.child(mAuth.getCurrentUser().getUid()).push();
                myRefChild.child("Receiver").setValue(movie.getName());
                myRefChild.child("Amount").setValue(movie.getAmount());
                mRefPre.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                if (d.child("Email").getValue().equals(mAuth.getCurrentUser().getEmail())) {
                                    ;
                                    myRefReq = myRefReq.child(d.child("Phone").getValue().toString());
                                    myRefReq.removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    RequestModel getItem(int id) {
        return mData.get(id);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        public Button btn_request;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            genre = (TextView) itemView.findViewById(R.id.number);
            btn_request = (Button) itemView.findViewById(R.id.pay);
        }
    }
}
