package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.StateAdapter;
import com.greensquad.atforecast.models.State;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StateListFragment extends Fragment {
    private static final String ARG_STATE_LIST = "states";
    private static final String LOG_TAG = StateListFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    public StateListFragment() {}

    public static StateListFragment newInstance(ArrayList<State> shelters) {
        StateListFragment fragment = new StateListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_STATE_LIST, shelters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state_list, container, false);
        final ArrayList<State> states = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);

        Call<List<State>> call = apiService.getStates();
        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, Response<List<State>> response) {
                List<State> statesList = response.body();

                for (State state : statesList) {
                    states.add(state);
                }
                mAdapter = new StateAdapter(states);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<State>>call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
        return view;
    }

}
