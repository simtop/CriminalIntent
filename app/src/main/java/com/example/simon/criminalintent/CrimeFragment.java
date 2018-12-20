package com.example.simon.criminalintent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CrimeFragment extends android.support.v4.app.Fragment {

    private RecyclerView mCrimeRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

            mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);

            mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return view;
    }
}