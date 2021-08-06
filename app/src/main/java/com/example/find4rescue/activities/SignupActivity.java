package com.example.find4rescue.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.models.Comments;
import com.example.find4rescue.models.Risk;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import java.util.List;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    EditText etUsername;
    EditText etPassword;
    Switch swRescuerOrNo;
    Button btnSignup;
    Boolean rescuerOrNo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etSignupUsername);
        etPassword = findViewById(R.id.etSignupPassword);
        swRescuerOrNo = findViewById(R.id.swRescuerOrNo);

        swRescuerOrNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rescuerOrNo = true;
                } else {
                    rescuerOrNo = false;
                }
            }
        });

        btnSignup = findViewById(R.id.btnSignSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username, password, rescuerOrNo);
            }
        });


    }

    private void signupUser(String username, String password, Boolean rescuerOrNo) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("RescuerOrNo", rescuerOrNo);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Sign Up issue: " + e);
                    Toast.makeText(SignupActivity.this, "Issue with Sign Up!", Toast.LENGTH_SHORT).show();
                } else {

                    ParseQuery<Risk> query = ParseQuery.getQuery(Risk.class);
                    query.findInBackground(new FindCallback<Risk>() {
                        @Override
                        public void done(List<Risk> objects, ParseException e) {
                            for (Risk risk : objects) {
                                JSONArray usernames = risk.getUsernames();
                                JSONArray dealtOrNot = risk.getDealtOrNot();
                                usernames.put(username);
                                dealtOrNot.put("False");
                                risk.setDealtOrNot(dealtOrNot);
                                risk.setUsernames(usernames);
                                risk.saveInBackground();
                            }
                        }
                    });

                    goMainActivity();
                    Toast.makeText(SignupActivity.this, "Successful Sign Up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}