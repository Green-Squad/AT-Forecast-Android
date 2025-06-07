package com.greensquad.atforecast.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.fragments.ShelterListFragment;
import com.greensquad.atforecast.models.State;

import java.util.ArrayList;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {
    static final String LOG_TAG = StateAdapter.class.getSimpleName();
    private ArrayList<State> mStates;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView temps;
        public ImageView icon;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.state_name);
            temps = v.findViewById(R.id.state_avg_temps);
            icon = v.findViewById(R.id.state_icon);
        }
    }

    public void add(int position, State item) {
        mStates.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mStates.remove(position);
        notifyItemRemoved(position);
    }

    public void refill(ArrayList<State> states) {
        mStates.clear();
        mStates.addAll(states);
        notifyDataSetChanged();
    }

    public StateAdapter(ArrayList<State> states) {
        mStates = states;
    }

    @NonNull
    @Override
    public StateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_state, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final State state = mStates.get(position);
        final String stateName = state.getName();

        int imgId = context.getResources().getIdentifier(state.getImageName(), "drawable", context.getPackageName());

        holder.icon.setImageResource(imgId);
        holder.name.setText(stateName);
        holder.temps.setText(context.getString(R.string.state_average_temps,
                state.getAverageHigh(), state.getAverageLow()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String shelterList = gson.toJson(state.getSheltersFromDb());

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ShelterListFragment shelterListFragment = ShelterListFragment.newInstance(stateName, shelterList);
                FragmentManager manager = activity.getSupportFragmentManager();
                manager.beginTransaction().setCustomAnimations(
                        R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit)
                    .replace(
                        R.id.fragment_main,
                        shelterListFragment,
                        shelterListFragment.getTag()
                ).addToBackStack("shelter_list_fragment").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStates.size();
    }

}