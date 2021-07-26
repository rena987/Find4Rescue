package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.find4rescue.R;
import com.example.find4rescue.databinding.FragmentSearchDetailBinding;
import com.example.find4rescue.models.Risk;
import com.parse.ParseFile;

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

        ParseFile image = risk.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(binding.dtImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}