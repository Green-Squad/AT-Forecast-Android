package com.greensquad.atforecast.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.fragments.ShelterDetailFragment;
import com.greensquad.atforecast.models.Shelter;

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
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ShelterDetailFragment shelterDetailFragment = ShelterDetailFragment.newInstance(shelter.getId());
                FragmentManager manager = activity.getSupportFragmentManager();
                manager.beginTransaction().replace(
                        R.id.fragment_main,
                        shelterDetailFragment,
                        shelterDetailFragment.getTag()
                ).addToBackStack("shelter_detail_fragment").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShelters.size();
    }

}