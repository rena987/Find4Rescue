package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.adapters.RiskAdapter;
import com.example.find4rescue.models.Risk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    RecyclerView rvRisks;
    List<Risk> risks;
    RiskAdapter riskAdapter;
    FloatingActionButton fabAddRisk;
    Switch stSort;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        risks = new ArrayList<>();
        rvRisks = view.findViewById(R.id.rvRisks);
        fabAddRisk = view.findViewById(R.id.fabAddRisk);
        stSort = view.findViewById(R.id.stSort);

        queryRisks(false);

        stSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                queryRisks(isChecked);
            }
        });

        riskAdapter = new RiskAdapter(getContext(), risks, onRiskClickListener);
        rvRisks.setAdapter(riskAdapter);
        rvRisks.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.flContainer, new CreateSearchFragment()).commit();
            }
        });

    }

    RiskAdapter.OnRiskClickListener onRiskClickListener = new RiskAdapter.OnRiskClickListener() {
        @Override
        public void onRiskClick(int position) {
            SearchDetailFragment fragment = new SearchDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("risk", risks.get(position));
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    };

    private void queryRisks(boolean isChecked) {
        ParseQuery<Risk> query = ParseQuery.getQuery(Risk.class);
        query.include(Risk.KEY_RESCUER);
        query.setLimit(20);

        if (isChecked) {
            query.orderByAscending("NumOfRescuers");
        } else {
            query.orderByDescending("createdAt");
        }

        query.findInBackground(new FindCallback<Risk>() {
            @Override
            public void done(List<Risk> objects, ParseException e) {
                riskAdapter.clear();
                if (e != null) {
                    Log.e(TAG, "Issue with getting risks: " + e);
                    return;
                }

                for (Risk risk : objects) {
                    Log.i(TAG, "Risk: " + risk.getAddress() + ", username: " + risk.getRescuer().getUsername());
                }

                risks.addAll(objects);
                riskAdapter.notifyDataSetChanged();
            }
        });
    }

}