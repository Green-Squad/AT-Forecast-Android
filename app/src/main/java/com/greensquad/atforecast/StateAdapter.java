package com.greensquad.atforecast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {
    private ArrayList<State> mStates;

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
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        State state = mStates.get(position);
        String stateName = state.getName();
        holder.name.setText(stateName);
        holder.temps.setText(state.getAverageHigh() + "° / " + state.getAverageLow()+ "°");
    }

    @Override
    public int getItemCount() {
        return mStates.size();
    }

}