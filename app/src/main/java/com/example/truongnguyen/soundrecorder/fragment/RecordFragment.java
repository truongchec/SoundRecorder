package com.example.truongnguyen.soundrecorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.truongnguyen.soundrecorder.R;
import com.example.truongnguyen.soundrecorder.RecordingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        View recordView=inflater.inflate(R.layout.fragment_record,container,false);
        mChronometer=(Chronometer) recordView.findViewById(R.id.chronometer);
        mRecordingPrompt=(TextView)recordView.findViewById(R.id.recording_status_text);
        mRecordButton=(FloatingActionButton)recordView.findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording=!mStartRecording;
            }
        });
//        mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
//        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
//        mPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onPauseRecord(mPauseRecording);
//                mPauseRecording = !mPauseRecording;
//            }
//        });





        return recordView;
    }

    private void onRecord(boolean start) {
        Intent intent=new Intent(getActivity(),RecordingService.class);
        if (start){
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            ///



        }
    }
}
