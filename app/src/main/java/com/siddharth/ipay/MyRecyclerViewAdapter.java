package com.siddharth.ipay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Contact> mData;
    private LayoutInflater mInflater;
    private Context context;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact movie = mData.get(position);
        holder.title.setText(movie.getName());
        holder.genre.setText(movie.getNumber());
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, year, genre;
        protected Button btn_pay, btn_request;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            genre = (TextView) itemView.findViewById(R.id.number);
            btn_pay = (Button) itemView.findViewById(R.id.pay);
            btn_request = (Button) itemView.findViewById(R.id.request);
            btn_pay.setOnClickListener(this);
            btn_request.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btn_pay.getId()) {
                loadFragment(new PayFragment());
            } else if (v.getId() == btn_request.getId()) {
                Toast.makeText(v.getContext(), "Request", Toast.LENGTH_LONG).show();
            }
        }

        private void loadFragment(Fragment fragment) {
// create a FragmentManager
            FragmentManager fm = ((Activity) (context)).getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
            fragmentTransaction.replace(R.id.main_recyclerview, fragment);
            fragmentTransaction.commit(); // save the changes
        }
    }


}
