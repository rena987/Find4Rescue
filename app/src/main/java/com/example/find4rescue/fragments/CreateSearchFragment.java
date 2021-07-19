package com.example.find4rescue.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.models.Risk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CreateSearchFragment extends Fragment {

    private EditText etLatitude;
    private EditText etLongitude;
    private EditText etCreateAddress;
    private EditText etCreateType;
    private EditText etCreateDescription;
    private Button btnAttachImage;
    private Button btnTakePicture;
    private ImageView ivCreateImage;
    private Button btnCreateAddRisk;

    public static final String TAG = "MakeRisk";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 37;
    public File photoFile;
    public String photoFileName = "photo.jpg";

    public CreateSearchFragment() {
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
        return inflater.inflate(R.layout.fragment_create_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
        etCreateAddress = view.findViewById(R.id.etCreateAddress);
        etCreateType = view.findViewById(R.id.etCreateType);
        etCreateDescription = view.findViewById(R.id.etCreateDescription);
        btnAttachImage = view.findViewById(R.id.btnAttachImage);
        btnTakePicture = view.findViewById(R.id.btnTakePicture);
        ivCreateImage = view.findViewById(R.id.ivCreateImage);
        btnCreateAddRisk = view.findViewById(R.id.btnCreateAddRisk);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnCreateAddRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Attempt to add risk...");
                clearBackgroundFields();
                if (!checkEmptyFields()) {
                    return;
                }
                saveRisk();
            }
        });
    }

    private void saveRisk() {
        Log.d(TAG, "Attempting to save risk...");
        Risk risk = new Risk();
        risk.setCoordinates(etLatitude.getText().toString() + "," + etLongitude.getText().toString());
        risk.setAddress(etCreateAddress.getText().toString());
        risk.setType(etCreateType.getText().toString());
        risk.setDescription(etCreateDescription.getText().toString());
        risk.setRescuer(ParseUser.getCurrentUser());


        Bitmap bitmap = ((BitmapDrawable) ivCreateImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        ParseFile image = new ParseFile(photoFileName, bitmapBytes);
        risk.setImage(image);


        Log.d(TAG, "Risk: " + risk.getAddress() + ", " + risk.getRescuer().getUsername());

        risk.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving: " + e);
                    return;
                }
                Log.d(TAG, "Risk was saved successfully!");
                clearTextualFields();
                getFragmentManager().beginTransaction().replace(R.id.flContainer, new SearchFragment()).commit();
            }
        });
    }

    private void clearTextualFields() {
        etLatitude.setText("");
        etLongitude.setText("");
        etCreateAddress.setText("");
        etCreateType.setText("");
        etCreateDescription.setText("");
        ivCreateImage.setImageResource(0);
    }

    private void clearBackgroundFields() {
        etLatitude.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        etLongitude.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        etCreateAddress.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        etCreateType.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        etCreateDescription.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        Log.d(TAG, "Attempting to clear background risk...");
    }

    private Boolean checkEmptyFields() {

        Boolean checkEmpty = true;
        if (etLatitude.getText().toString().isEmpty()) {
            etLatitude.setBackground(new ColorDrawable(Color.parseColor("#F08080")));
            checkEmpty = false;
        }
        if (etLongitude.getText().toString().isEmpty()) {
            etLongitude.setBackground(new ColorDrawable(Color.parseColor("#F08080")));
            checkEmpty = false;
        }
        if (etCreateAddress.getText().toString().isEmpty()) {
            etCreateAddress.setBackground(new ColorDrawable(Color.parseColor("#F08080")));
            checkEmpty = false;
        }
        if (etCreateType.getText().toString().isEmpty()) {
            etCreateType.setBackground(new ColorDrawable(Color.parseColor("#F08080")));
            checkEmpty = false;
        }
        if (etCreateDescription.getText().toString().isEmpty()) {
            etCreateDescription.setBackground(new ColorDrawable(Color.parseColor("#F08080")));
            checkEmpty = false;
        }
        if (ivCreateImage.getDrawable() == null) {
            Toast.makeText(getContext(), "You haven't attached an iamge!", Toast.LENGTH_SHORT).show();
            checkEmpty = false;
        }

        Log.d(TAG, "Attempting to clear textual risk...");
        return checkEmpty;
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivCreateImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}