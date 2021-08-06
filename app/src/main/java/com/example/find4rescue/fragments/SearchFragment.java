package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.find4rescue.R;
import com.example.find4rescue.adapters.RiskAdapter;
import com.example.find4rescue.models.Risk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

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

        riskAdapter = new RiskAdapter(getContext(), risks, onRiskClickListener, onRiskLongClickListener);
        rvRisks.setAdapter(riskAdapter);
        rvRisks.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, new CreateSearchFragment())
                        .commit();
            }
        });

    }

    RiskAdapter.OnRiskClickListener onRiskClickListener = new RiskAdapter.OnRiskClickListener() {
        @Override
        public void onRiskClick(int position, Risk risk, ImageView imageView) {
            SearchDetailFragment fragment = new SearchDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("risk", risks.get(position));
            bundle.putString("transitionName", ViewCompat.getTransitionName(imageView));
            fragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                    .replace(R.id.flContainer, fragment)
                    .commit();
        }
    };

    RiskAdapter.OnRiskLongClickListener onRiskLongClickListener = new RiskAdapter.OnRiskLongClickListener() {
        @Override
        public boolean onRiskLongClicked(int position, Risk risk) {
            if (ParseUser.getCurrentUser().getUsername().equals(risk.getRescuer().getUsername())) {
                Log.d(TAG, "Long clicked: " + position);
                risks.remove(position);
                riskAdapter.notifyDataSetChanged();
                deleteRisk(risk);
                return true;
            } else {
                Log.d(TAG, "No access to risk!");
                return false;
            }
        }
    };

    private void deleteRisk(Risk risk) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Risk");
        query.getInBackground(risk.getObjectId(), (object, e) -> {
            if (e == null) {
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Log.d(TAG,"Delete Successful");
                    }else{
                        Log.d(TAG,"Error: "+e2.getMessage());
                    }
                });
            }else{
                Log.d(TAG, "Error: "+e.getMessage());
            }
        });
    }

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
                risks.addAll(objects);
                riskAdapter.notifyDataSetChanged();
            }
        });
    }
}