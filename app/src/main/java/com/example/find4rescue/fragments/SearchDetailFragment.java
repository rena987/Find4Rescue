package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.find4rescue.R;
import com.example.find4rescue.databinding.FragmentSearchDetailBinding;
import com.example.find4rescue.models.Risk;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchDetailFragment extends Fragment {

    FragmentSearchDetailBinding binding;

    public SearchDetailFragment() {
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
        binding = FragmentSearchDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Risk risk = getArguments().getParcelable("risk");
        binding.dtAddress.setText("Address: " + risk.getAddress());
        binding.dtType.setText("Type: " + risk.getType());
        binding.dtRescuer.setText("Reported by: " + risk.getRescuer().getUsername());
        binding.dtDescription.setText(risk.getDescription());
        binding.dtNumOfRescuers.setText("Existing Rescuers: " + risk.getNumOfRescuers());

        ParseFile image = risk.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(binding.dtImage);
        }

        Log.d("SearchDetailFragment", "1 Dealt or Not: " + risk.getDealtOrNot());

        if (risk.getDealtOrNot()) {
            binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_filled_button));
        }

        binding.ivPlotMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment fragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("coordinates", risk.getCoordinates());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        binding.ivDealtRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealOrUndeal(risk);
            }
        });

        binding.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentFragment fragment = new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("risk", risk);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

    }

    private void DealOrUndeal(Risk risk) {
        Log.d("SearchDetailFragment", "2 Dealt or Not: " + risk.getDealtOrNot());
        if (risk.getDealtOrNot()) {
            risk.setDealtOrNot(false);
            risk.setNumOfRescuers(risk.getNumOfRescuers() - 1);
            risk.saveInBackground();
            binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_unfilled_button));
        } else {
            risk.setDealtOrNot(true);
            risk.setNumOfRescuers(risk.getNumOfRescuers() + 1);
            risk.saveInBackground();
            binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_filled_button));
        }
        Log.d("SearchDetailFragment", "3 Dealt or Not: " + risk.getDealtOrNot());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}