package com.example.truongnguyen.soundrecorder.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.truongnguyen.soundrecorder.DBHelper;
import com.example.truongnguyen.soundrecorder.R;
import com.example.truongnguyen.soundrecorder.RecordingItem;

import com.example.truongnguyen.soundrecorder.fragment.PlaybackFragment;
import com.example.truongnguyen.soundrecorder.listeners.OnDatabaseChangedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> implements OnDatabaseChangedListener {
    private static final String LOG_TAG = "FileViewerAdapter";

    private DBHelper mDatabase;

    RecordingItem item;
    Context mContext;
    LinearLayoutManager llm;

    public FileViewerAdapter(Context context, LinearLayoutManager linearLayoutManager) {
        super();
        mContext = context;
        mDatabase = new DBHelper(mContext);
        mDatabase.setOnDatabaseChangedListener(this);
        llm = linearLayoutManager;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordingsViewHolder holder, int position) {
        item = getItem(position);
        long itemDuration = item.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);


        holder.vName.setText(item.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        mContext,
                        item.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlaybackFragment playbackFragment =
                            new PlaybackFragment().newInstance(getItem(holder.getAdapterPosition()));
                    FragmentTransaction transaction = ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(mContext.getString(R.string.dialog_file_share));
                entrys.add(mContext.getString(R.string.dialog_file_rename));
                entrys.add(mContext.getString(R.string.dialog_file_delete));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.dialog_title_options));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            shareFileDialog(holder.getAdapterPosition());
                        }if (which == 1) {
                            renameFileDialog(holder.getAdapterPosition());
                        } else if (which == 2) {
                            deleteFileDialog(holder.getAdapterPosition());
                        }
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });


    }

    private void deleteFileDialog(int adapterPosition) {
    }

    private void renameFileDialog(int adapterPosition) {
    }

    private void shareFileDialog(int adapterPosition) {
    }

    //TODO
    public void removeOutOfApp(String filePath) {
        //user deletes a saved recording out of the application through another application
    }

    @NonNull
    @Override
    public RecordingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
      mContext=parent.getContext();


        return new RecordingsViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public RecordingItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        notifyItemInserted(getItemCount() - 1);
        llm.scrollToPosition(getItemCount() - 1);

    }

    @Override
    public void onDatabaseEntryRenamed() {

    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;

        public RecordingsViewHolder(View v) {
            super(v);
            vName=(TextView) v.findViewById(R.id.file_name_text);
            vLength=(TextView) v.findViewById(R.id.file_length_text);
            vDateAdded=(TextView) v.findViewById(R.id.file_date_added_text);
            cardView=v.findViewById(R.id.card_view);

        }
    }
    public void remove(int position){
        File file = new File(getItem(position).getFilePath());
        file.delete();

        Toast.makeText(
                mContext,
                String.format(
                        mContext.getString(R.string.toast_file_delete),
                        getItem(position).getName()
                ),
                Toast.LENGTH_SHORT
        ).show();

        mDatabase.removeItemWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }
    public void rename(int position, String name){
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/SoundRecorder/" + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot rename file.
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_exists), name),
                    Toast.LENGTH_SHORT).show();

        } else {
            //file name is unique, rename file
            File oldFilePath = new File(getItem(position).getFilePath());
            oldFilePath.renameTo(f);
            mDatabase.renameItem(getItem(position), name, mFilePath);
            notifyItemChanged(position);
        }
    }
}
