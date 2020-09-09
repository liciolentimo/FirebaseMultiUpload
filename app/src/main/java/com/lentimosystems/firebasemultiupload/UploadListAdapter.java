package com.lentimosystems.firebasemultiupload;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList) {
        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = fileNameList.get(position);
        holder.fileName.setText(fileName);
        String fileDone = fileDoneList.get(position);
        if (fileDone.equals("Uploading")){
            holder.fileDone.setImageResource(R.drawable.progress);
        } else {
            holder.fileDone.setImageResource(R.drawable.checked);
        }
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView fileName;
        public ImageView fileDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.txtFilename);
            fileDone = itemView.findViewById(R.id.imgLoading);
        }
    }
}
