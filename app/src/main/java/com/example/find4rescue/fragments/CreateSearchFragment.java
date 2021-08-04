package com.example.find4rescue.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.find4rescue.models.Comments;
import com.example.find4rescue.models.Risk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
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
    public final static int PICK_PHOTO_REQUEST_CODE = 1046;
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("bitmap");
            ivCreateImage.setImageBitmap(bitmap);
        }

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
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
            }
        });

        Comments comment = new Comments();
        comment.setRisk(risk);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving: " + e);
                }
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.flContainer, new SearchFragment()).commit();
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

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivCreateImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                Bitmap selectedImage = loadFromUri(photoUri);

                ivCreateImage.setImageBitmap(selectedImage);
            } else {
                Toast.makeText(getContext(), "Invalid uploaded picture!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPickPhoto(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}