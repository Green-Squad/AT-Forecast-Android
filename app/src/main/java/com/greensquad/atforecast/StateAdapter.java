package com.greensquad.atforecast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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

        RelativeLayout r = (RelativeLayout) holder.name.getParent();

        holder.name.setText(stateName);
        holder.temps.setText(state.getAverageHigh() + "° / " + state.getAverageLow()+ "°");

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String shelterList = gson.toJson(state.getShelters());

                Intent intent = new Intent(context, SheltersActivity.class);
                intent.putExtra("shelters", shelterList);
                intent.putExtra("stateName", stateName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStates.size();
    }

}