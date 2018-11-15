package com.example.truongnguyen.soundrecorder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


import com.example.truongnguyen.soundrecorder.activities.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LicemsesFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater dialogInflater=getActivity().getLayoutInflater();
        View openSourceLicenseView=dialogInflater.inflate(R.layout.fragment_licenses,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(openSourceLicenseView).setTitle((getString(R.string.dialog_title_licenses)))
                .setNeutralButton(android.R.string.ok,null);

        return builder.create();
    }
}
