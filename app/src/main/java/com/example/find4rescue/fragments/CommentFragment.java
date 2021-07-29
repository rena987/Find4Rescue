package com.example.find4rescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;


import android.widget.EditText;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.models.Comments;
import com.example.find4rescue.models.Risk;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    EditText etMessage;
    AppCompatImageButton ibSend;
    RecyclerView rvComments;

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
        Risk risk = getArguments().getParcelable("risk");

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                String username = ParseUser.getCurrentUser().getUsername();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
                query.whereEqualTo("CommentedRisk", risk);
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
}