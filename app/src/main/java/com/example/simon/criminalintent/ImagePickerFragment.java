package com.example.simon.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.simon.criminalintent.utils.PictureUtils;

public class ImagePickerFragment extends DialogFragment {
    private static final String ARG_PHOTO = "photo";

    private ImageView mPhotoView;


    public static ImagePickerFragment newInstance(String pathPhoto) {
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO, pathPhoto);

        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String pathPhoto = getArguments().getString(ARG_PHOTO);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);

        mPhotoView = v.findViewById(R.id.photo_big);

        Bitmap bitmap = PictureUtils.getScaledBitmap(pathPhoto, getActivity());


        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }
}
