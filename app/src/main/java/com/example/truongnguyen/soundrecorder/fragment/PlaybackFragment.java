package com.example.truongnguyen.soundrecorder.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.truongnguyen.soundrecorder.RecordingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.DialogFragment;

public class PlaybackFragment  extends DialogFragment {

    private static final String LOG_TAG = "PlaybackFragment";

    private static final String ARG_ITEM = "recording_item";
    private RecordingItem item;

    private Handler mHandler = new Handler();

    private MediaPlayer mMediaPlayer = null;

    private SeekBar mSeekBar = null;
    private FloatingActionButton mPlayButton = null;
    private TextView mCurrentProgressTextView = null;
    private TextView mFileNameTextView = null;
    private TextView mFileLengthTextView = null;


    private boolean isPlaying = false;


    long minutes = 0;
    long seconds = 0;

    public PlaybackFragment newInstance(RecordingItem item) {
        PlaybackFragment f = new PlaybackFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM, item);



        return f;
    }
}
