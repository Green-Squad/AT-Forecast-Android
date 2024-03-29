package com.greensquad.atforecast.adapters;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.fragments.ShelterDetailFragment;
import com.greensquad.atforecast.models.Shelter;

import java.util.ArrayList;

public class SheltersAdapter extends RecyclerView.Adapter<SheltersAdapter.ViewHolder> {
    static final String LOG_TAG = SheltersAdapter.class.getSimpleName();

    private ArrayList<Shelter> mShelters;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        TextView mileage;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.shelters_shelter_name);
            mileage = v.findViewById(R.id.shelters_shelter_mileage);
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

    @NonNull
    @Override
    public SheltersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_shelter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Shelter shelter = mShelters.get(position);
        final String shelterName = shelter.getName();
        final String shelterMileage = shelter.getMileage().toString();

        holder.name.setText(shelterName);
        holder.mileage.setText(shelterMileage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        ShelterDetailFragment shelterDetailFragment = ShelterDetailFragment.newInstance(shelter.getShelterId());
                        FragmentManager manager = activity.getSupportFragmentManager();
                        manager.beginTransaction().setCustomAnimations(
                                R.anim.fragment_slide_left_enter,
                                R.anim.fragment_slide_left_exit,
                                R.anim.fragment_slide_right_enter,
                                R.anim.fragment_slide_right_exit)
                                .replace(
                                        R.id.fragment_main,
                                        shelterDetailFragment,
                                        shelterDetailFragment.getTag()
                                ).addToBackStack("shelter_detail_fragment").commit();
                    }
                }, 50);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShelters.size();
    }

}