package com.example.parsetagram;

import static android.app.Activity.RESULT_OK;
import static com.example.parsetagram.BaseFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class ProfileFragment extends BaseFragment {
    TextView tvUsername;
    ImageView ivProfilePhoto;
    public User user = (User) User.getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Glide.with(getContext()).load(takenImage).circleCrop().into(ivProfilePhoto);
                user.setProfilePhoto(new ParseFile(photoFile));
                user.saveInBackground();

            }
            else {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsernameFragment);
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();

                //take picture


            }
        });

        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                user = (User) object;
                displayuserInfo();
            }
        });

    }
    public void displayuserInfo() {

        tvUsername.setText(user.getUsername());
        ParseFile profilePhoto = user.getProfilePhoto();
        if(profilePhoto != null) {
            Glide.with(getContext()).load(profilePhoto.getUrl()).circleCrop().into(ivProfilePhoto);
        }
    }

}