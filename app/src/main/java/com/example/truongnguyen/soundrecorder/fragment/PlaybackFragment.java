package com.example.truongnguyen.soundrecorder.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.truongnguyen.soundrecorder.RecordingItem;
import com.example.truongnguyen.soundrecorder.activities.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PlaybackFragment extends DialogFragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(ARG_ITEM);

        long itemDuration = item.getLength();
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MINUTES.toSeconds(minutes);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_media_playback, null);

        mFileNameTextView = (TextView) view.findViewById(R.id.file_name_text_view);
        mFileLengthTextView = (TextView) view.findViewById(R.id.file_length_text_view);
        mCurrentProgressTextView = (TextView) view.findViewById(R.id.current_progress_text_view);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        ColorFilter filter = new LightingColorFilter(getResources().getColor(R.color.primary), getResources().getColor(R.color.primary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition()) -
                            TimeUnit.MINUTES.toSeconds(minutes);
                    mCurrentProgressTextView.setText(String.format("%02d:%02d",
                            minutes, seconds));
                    updateSeekBar();
                } else if (mMediaPlayer == null && fromUser) {
                    prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    mHandler.removeCallbacks(mRunnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mMediaPlayer != null) {
                    mHandler.removeCallbacks(mRunnable);
                    mMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes, seconds));
                    updateSeekBar();
                }

            }
        });
        mPlayButton = (FloatingActionButton) view.findViewById(R.id.fab_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });
        mFileNameTextView.setText(item.getName());
        mFileLengthTextView.setText(String.format("%02d:%02d", minutes, seconds));

        builder.setView(view);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);


        AlertDialog alertDialog = (AlertDialog) getDialog();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        isPlaying = !isPlaying;

        mCurrentProgressTextView.setText(mFileLengthTextView.getText());
        mSeekBar.setProgress(mSeekBar.getMax());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void onPlay(boolean isPlaying) {
        if (!isPlaying) {

            if (mMediaPlayer == null) {
                startPlaying();
            } else {
                resumePlaying();
            }

        } else {

            pausePlaying();
        }
    }

    private void pausePlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();
    }

    private void resumePlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

    private void startPlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();


        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void prepareMediaPlayerFromPoint(int progress) {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(mCurrentPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) - TimeUnit.MINUTES.toSeconds(minutes);
                mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes, seconds));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
}
