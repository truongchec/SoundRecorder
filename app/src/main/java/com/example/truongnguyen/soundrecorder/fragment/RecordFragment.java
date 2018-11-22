package com.example.truongnguyen.soundrecorder.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;


import com.example.truongnguyen.soundrecorder.RecordingService;
import com.example.truongnguyen.soundrecorder.activities.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class RecordFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;
    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0;


    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View recordView = inflater.inflate(R.layout.fragment_record, container, false);
        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);
        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPermissionOnRecord();

            }
        });


        return recordView;
    }

    private void requestPermissionOnRecord() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
          //chua duoc cap quyen

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 100);


        } else {
            //da dc cap quyen thi...

            onRecord(mStartRecording);
            mStartRecording = !mStartRecording;

        }
    }

    private void onRecord(boolean start) {
        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (start) {
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();

            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }

            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");

                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }
                    mRecordPromptCount++;
                }
            });
            getActivity().startService(intent);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
