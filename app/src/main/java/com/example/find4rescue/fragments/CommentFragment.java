package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.widget.EditText;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.adapters.CommentsAdapter;
import com.example.find4rescue.models.Risk;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    public static final String TAG = "CommentFragment";
    EditText etMessage;
    AppCompatImageButton ibSend;
    RecyclerView rvComments;
    ParseQuery<ParseObject> query;
    List<String> usernames;
    List<String> messages;
    CommentsAdapter adapter;

    public CommentFragment() {
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
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        etMessage = view.findViewById(R.id.etMessage);
        ibSend = view.findViewById(R.id.ibSend);
        rvComments = view.findViewById(R.id.rvComments);
        usernames = new ArrayList<>();
        messages = new ArrayList<>();

        Risk risk = getArguments().getParcelable("risk");
        query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("CommentedRisk", risk);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                JSONArray usernames_arr_ = object.getJSONArray("Usernames");
                JSONArray messages_arr_ = object.getJSONArray("Messages");
                for (int i = 0; i < usernames_arr_.length(); i++) {
                    try {
                        usernames.add(usernames_arr_.getString(i));
                        messages.add(messages_arr_.getString(i));
                        adapter = new CommentsAdapter(getContext(), usernames, messages);
                        rvComments.setAdapter(adapter);
                        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
                        refreshComments();
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }
        });


        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                String username = ParseUser.getCurrentUser().getUsername();

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            JSONArray usernames = object.getJSONArray("Usernames");
                            usernames.put(username);
                            object.put("Usernames", usernames);

                            JSONArray messages = object.getJSONArray("Messages");
                            messages.put(message);
                            object.put("Messages", messages);

                            object.saveInBackground();
                            refreshComments();

                            etMessage.setText("");
                        } else {
                            // something went wrong
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void refreshComments() {
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                messages.clear();
                usernames.clear();

                JSONArray usernames_arr_ = object.getJSONArray("Usernames");
                JSONArray messages_arr_ = object.getJSONArray("Messages");
                for (int i = 0; i < usernames_arr_.length(); i++) {
                    try {
                        usernames.add(usernames_arr_.getString(i));
                        messages.add(messages_arr_.getString(i));
                        Log.d(TAG, "Refreshing: " + messages);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }
        });
    }

}