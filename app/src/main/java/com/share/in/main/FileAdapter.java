package com.share.in.main;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.share.in.R;

import java.util.ArrayList;
import android.util.SparseBooleanArray;
public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private FileActivity fa;
    private ArrayList<FileModel> imageList;
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private static OnItemClickListener onItemClickListener;

    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;

    public FileAdapter(Context context, ArrayList<FileModel> imageList,FileActivity fa) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IMAGE_LIST) {;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
            return new ImageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_picker_list, parent, false);
            return new ImagePickerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_LIST;
    }

    @Override
    public  void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {
            ;
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;

            viewHolder.title.setText(imageList.get(position).getTitle());
            viewHolder.position = position;
            viewHolder.size.setText(imageList.get(position).getSize());
            //viewHolder.image.setVideoPath(imageList.get(position).getImage());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.isMemoryCacheable();
            if (imageList.get(position).getFileType().equals("video")){
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(imageList.get(position).getPath())
                        .placeholder(R.color.codeGray)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.image);
            viewHolder.play_button.setVisibility(View.VISIBLE);
        }
            else {
                Glide.with(context)
                        .load(imageList.get(position).getImage())
                        .centerCrop()
                        .into(viewHolder.image);
                viewHolder.play_button.setVisibility(View.INVISIBLE);
                if(imageList.get(position).getFileType().equals("folder"))
                    viewHolder.size.setVisibility(View.INVISIBLE);
            }
            if (imageList.get(position).isSelected()) {
                viewHolder.checkBox.setChecked(true);
            } else {;
                viewHolder.checkBox.setChecked(false);
            }
            viewHolder.bind(position);
        } else {;
            ImagePickerViewHolder viewHolder = (ImagePickerViewHolder) holder;
            viewHolder.image.setImageResource(imageList.get(position).getResImg());
            viewHolder.title.setText(imageList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image,play_button;
        CheckBox checkBox;
        TextView title,duration,size;
        int position;
        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            play_button= itemView.findViewById(R.id.play1);
            checkBox = itemView.findViewById(R.id.circle);
            title=itemView.findViewById(R.id.vidname);
            size=itemView.findViewById(R.id.vidsize);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("onBindViewHolder", "123");

                }
            });
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }
this.position=position;
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.e("onBindViewHolder", "456");
            /*if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }*/
//            new FileActivity(imageList.get(position).getPath());
        }
    }

    public class ImagePickerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ImagePickerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

}
