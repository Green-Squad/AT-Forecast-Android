package com.greensquad.atforecast;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SheltersAdapter extends RecyclerView.Adapter<SheltersAdapter.ViewHolder> {
    static final String LOG_TAG = SheltersAdapter.class.getSimpleName();
    private ArrayList<Shelter> mShelters;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView mileage;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.shelters_shelter_name);
            mileage = (TextView) v.findViewById(R.id.shelters_shelter_mileage);
        }
    }

    public void add(int position, Shelter item) {
        mShelters.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mShelters.remove(position);
        notifyItemRemoved(position);
    }

    public SheltersAdapter(ArrayList<Shelter> shelters) {
        mShelters = shelters;
    }

    @Override
    public SheltersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_shelter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Shelter shelter = mShelters.get(position);
        final String shelterName = shelter.getName();
        final String shelterMileage = shelter.getMileage().toString();

        RelativeLayout r = (RelativeLayout) holder.name.getParent();

        holder.name.setText(shelterName);
        holder.mileage.setText(shelterMileage);

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(context, ShelterActivity.class);
            intent.putExtra("id", shelter.getId());
            context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShelters.size();
    }

}