package com.siddharth.ipay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Contact> mData;
    private LayoutInflater mInflater;
    private Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Payment");
    Personal person = new Personal();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Contact> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = mInflater.inflate(R.layout.contact_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Contact movie = mData.get(position);
        holder.title.setText(movie.getName());
        holder.genre.setText(movie.getNumber());
        holder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference myRefchild = myRef.child(mAuth.getCurrentUser().getUid()).push();
                        myRefchild.child("Receiver").setValue(movie.getName());
                        myRefchild.child("Amount").setValue(holder.et_amount.getText().toString().trim());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Fragment destFragment = new PaymentFragment();
                FragmentManager fragmentManager = ((Activity) (context)).getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_recyclerview, destFragment);
                fragmentTransaction.commit();
            }
        });
        holder.btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Request", Toast.LENGTH_LONG).show();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    Contact getItem(int id) {
        return mData.get(id);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public Button btn_pay, btn_request;
        public EditText et_amount;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            genre = (TextView) itemView.findViewById(R.id.number);
            btn_pay = (Button) itemView.findViewById(R.id.pay);
            btn_request = (Button) itemView.findViewById(R.id.request);
            et_amount = (EditText) itemView.findViewById(R.id.et_amount);
        }
    }


}
