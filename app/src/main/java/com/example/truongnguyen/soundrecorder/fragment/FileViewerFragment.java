package com.example.truongnguyen.soundrecorder.fragment;

import android.os.Bundle;

import com.example.truongnguyen.soundrecorder.adapter.FileViewerAdapter;

import androidx.fragment.app.Fragment;

public class FileViewerFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";

    private int position;
    private FileViewerAdapter mFileViewerAdapter;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

}
