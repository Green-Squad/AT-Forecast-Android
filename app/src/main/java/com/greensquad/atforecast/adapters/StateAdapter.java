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

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.state_name);
            temps = (TextView) v.findViewById(R.id.state_avg_temps);
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

    public StateAdapter(ArrayList<State> states) {
        mStates = states;
    }

    @Override
    public StateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_state, parent, false);
        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final State state = mStates.get(position);
        final String stateName = state.getName();

        holder.name.setText(stateName);
        holder.temps.setText("State average: " + state.getAverageHigh() + "° / " + state.getAverageLow()+ "°");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String shelterList = gson.toJson(state.getShelters());

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ShelterListFragment shelterListFragment = ShelterListFragment.newInstance(stateName, shelterList);
                FragmentManager manager = activity.getSupportFragmentManager();
                manager.beginTransaction().replace(
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