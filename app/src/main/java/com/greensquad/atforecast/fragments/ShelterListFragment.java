package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.SheltersAdapter;
import com.greensquad.atforecast.base.BackButtonSupportFragment;
import com.greensquad.atforecast.base.BaseFragment;
import com.greensquad.atforecast.models.Shelter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShelterListFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final String LOG_TAG = ShelterListFragment.class.getSimpleName();
    private static final String ARG_STATE_NAME = "state_name";
    private static final String ARG_SHELTER_LIST = "shelter_list";

    private String mStateName;
    private String mShelterList;

    public ShelterListFragment() {}

    public static ShelterListFragment newInstance(String stateName, String shelterList) {
        ShelterListFragment fragment = new ShelterListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATE_NAME, stateName);
        args.putString(ARG_SHELTER_LIST, shelterList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStateName = getArguments().getString(ARG_STATE_NAME);
            mShelterList = getArguments().getString(ARG_SHELTER_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelter_list, container, false);
        final ArrayList<Shelter> shelters = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shelters_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Shelter>>() {}.getType();
        List<Shelter> sheltersList = gson.fromJson(mShelterList, listType);
        ArrayList<Shelter> sheltersArrayList = new ArrayList<>(sheltersList);

        RecyclerView.Adapter mAdapter = new SheltersAdapter(sheltersArrayList);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected String getTitle() {
        return mStateName;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
