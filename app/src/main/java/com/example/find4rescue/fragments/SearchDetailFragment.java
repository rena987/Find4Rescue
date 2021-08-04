package com.example.find4rescue.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.find4rescue.R;
import com.example.find4rescue.databinding.FragmentSearchDetailBinding;
import com.example.find4rescue.models.Risk;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchDetailFragment extends Fragment {

    public static final String TAG = "SearchDetailFrag";

    public static final String KEY_RISK = "risk";
    public static final String KEY_TRANSITION = "transitionName";
    FragmentSearchDetailBinding binding;

    public SearchDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
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
        Risk risk = getArguments().getParcelable(KEY_RISK);
        String transitionName = getArguments().getString(KEY_TRANSITION);

        binding.dtAddress.setText("Address: " + risk.getAddress());
        binding.dtType.setText("Type: " + risk.getType());
        binding.dtRescuer.setText("Reported by: " + risk.getRescuer().getUsername());
        binding.dtDescription.setText(risk.getDescription());
        binding.dtNumOfRescuers.setText("Existing Rescuers: " + risk.getNumOfRescuers());

        ParseFile image = risk.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(binding.ivDisasterImage);
        }

        binding.ivDisasterImage.setTransitionName(transitionName);

        Picasso.get()
                .load(image.getUrl())
                .noFade()
                .into(binding.ivDisasterImage, new Callback() {
            @Override
            public void onSuccess() {
                startPostponedEnterTransition();
            }

            @Override
            public void onError(Exception e) {
                startPostponedEnterTransition();
            }
        });


        Log.d("SearchDetailFragment", "1 Dealt or Not: " + risk.getRescuer().getBoolean("DealtOrNot"));

        try {
            int index = getIndexOfUsername(ParseUser.getCurrentUser().getUsername(), risk);
            if (index != -1) {
                if (risk.getDealtOrNot().getBoolean(index)) {
                    binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_filled_button));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.ivPlotMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment fragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("address", risk.getAddress());
                bundle.putString("coordinates", risk.getCoordinates());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        binding.ivDealtRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DealOrUndeal(risk);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void DealOrUndeal(Risk risk) throws JSONException {
        String username = ParseUser.getCurrentUser().getUsername();
        int index = getIndexOfUsername(username, risk);
        if (index != -1) {
            updateDatabase(index, risk);
        }
    }

    private int getIndexOfUsername(String username, Risk risk) throws JSONException {
        int index = -1;
        for (int i = 0; i < risk.getUsernames().length(); i++) {
            if (risk.getUsernames().getString(i).equals(username)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private void updateDatabase(int index, Risk risk) throws JSONException {
        JSONArray dealtOrNots = risk.getDealtOrNot();
        boolean setDealt = dealtOrNots.getBoolean(index);
        dealtOrNots.remove(index);
        dealtOrNots.put(index, !setDealt);
        risk.setDealtOrNot(dealtOrNots);
        if (setDealt) {
            risk.setNumOfRescuers(risk.getNumOfRescuers() - 1);
            binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_unfilled_button));
        } else {
            risk.setNumOfRescuers(risk.getNumOfRescuers() + 1);
            binding.ivDealtRisk.setImageDrawable(getResources().getDrawable(R.drawable.heart_filled_button));
        }
        risk.saveInBackground();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}