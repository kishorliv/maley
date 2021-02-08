package com.example.maley.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maley.R;
import com.example.maley.api.NutritionApiClient;
import com.example.maley.api.NutritionApiInterface;
import com.example.maley.model.FoodPostBody;
import com.example.maley.model.Foods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Collections;

import com.example.maley.viewholder.AddFoodActivity;
import com.google.gson.Gson;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.visual_recognition.v3.model.ClassifyOptions;
import com.ibm.watson.visual_recognition.v3.VisualRecognition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class CameraFragment extends Fragment {
    private TextView mTakePhoto;
    private ProgressDialog mProgressDialog;
    private ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String API_KEY = "somekey";
    private static final String URL = "https://some-url.com";
    VisualRecognition mVisualRecognition;
    private String mCurrentPhotoPath;
    private Uri photoURI;
    private String nutritionString;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTakePhoto = view.findViewById(R.id.tv_take_photo);
        mImageView = view.findViewById(R.id.iv_camera);

        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        mVisualRecognition = new VisualRecognition("2018-03-19", authenticator);
        mVisualRecognition.setServiceUrl(URL);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check for camera
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Could not open camera! Check permissions.",Toast.LENGTH_SHORT).show();
                Log.d("TAG", "**************: Error occurred while creating the File");
            }
            // continue if file is created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);

                Log.d("TAG", "**************: Photo uri : "+ photoURI);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "Could not use the camera!",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void callNutritionApi(String query){
        FoodPostBody body = new FoodPostBody(query);
        // initilaize nutrition api service
        NutritionApiInterface nuService = NutritionApiClient.getClient().create(NutritionApiInterface.class);
        Call<Foods> foods = nuService.getNutritionResponse(body);

        foods.enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                mProgressDialog.dismiss();

                if(response.body() == null){
                    mProgressDialog.dismiss();
                    Log.d("TAG", "********************** Looks like the picture you sent is not a food!");
                    Toast.makeText(getActivity(), "Doesn't look like food!",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    nutritionString = response.body().toString();
                    Log.d("TAG", "------ Response nutrition name: " + response.body());
                    //mProgressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                    intent.putExtra("nutritionData", nutritionString);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.d("TAG", "******** Error response: " + t.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Fetching food nutrient data...");
            mProgressDialog.setTitle("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);

            //mImageView.setImageURI(photoURI);

            final String photoFilePath = mCurrentPhotoPath;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    InputStream imagesStream = null;
                    try {
                        imagesStream = new FileInputStream(photoFilePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                            .imagesFile(imagesStream)
                            .imagesFilename(photoFilePath)
                            .classifierIds(Collections.singletonList("food"))
                            .threshold((float) 0.6)
                            .build();
                    ClassifiedImages result = mVisualRecognition.classify(classifyOptions).execute().getResult();

                    Log.d("TAG", "**************: result :  " + result);

                    Gson gson = new Gson();
                    String json = gson.toJson(result);
                    String name = null;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("images");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("classifiers");
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("classes");
                        JSONObject jsonObject3 = jsonArray2.getJSONObject(0);
                        name = jsonObject3.getString("class");

                        Log.d("TAG", "**************: name :  " + name);

                        // call nutritionix food api
                        callNutritionApi(name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

}
