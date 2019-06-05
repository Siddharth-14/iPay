package com.siddharth.ipay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<String> finalList;

    public MainAdapter(List<String> finalList) {
        this.finalList = finalList;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder viewHolder, int i) {
        viewHolder.mNumber.setText(finalList.get(i));
    }

    @Override
    public int getItemCount() {
        return finalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
