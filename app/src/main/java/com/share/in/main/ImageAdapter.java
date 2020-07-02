
package com.share.in.main;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.share.in.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<ImageModel> imageList;
    private static OnItemClickListener onItemClickListener;
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;

    public ImageAdapter(Context context, ArrayList<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IMAGE_LIST) {;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list, parent, false);
            return new ImageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_picker_list, parent, false);
            return new ImagePickerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_LIST;
    }

    @Override
    public  void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {;
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;
            Glide.with(context)
                    .load(imageList.get(position).getImage())
                    .placeholder(R.color.codeGray)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.image);
            if(ImageModel.filePath.contains(imageList.get(position).getPath()+"/")) {
                //viewHolder.checkBox.setChecked(true);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }

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

    public class ImageListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        CheckBox checkBox;
        Button send;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.circle);
            itemView.setOnClickListener(v -> {
                //checkBox.setChecked(!checkBox.isChecked());
                int position=getAdapterPosition();
                send=(Button)((Activity)context).findViewById(R.id.button1);
                if(!ImageModel.filePath.contains(imageList.get(position).getPath()+"/")) {
                        checkBox.setVisibility(View.VISIBLE);
                        ImageModel.filePath.add(imageList.get(position).getPath() + "/");
                    } else {
                        if (imageList != null) {
                            checkBox.setVisibility(View.INVISIBLE);
                            ImageModel.filePath.removeIf((String fpath) -> fpath.startsWith(imageList.get(position).getPath()));
                        }
                    }
                    send.setText("Send ("+(
                            (ImageModel.filePath!=null ? ImageModel.filePath.size():0)+
                                    (VideoModel.filePath!=null ? VideoModel.filePath.size() : 0)+
                                    (AppModel.filePath!=null ? AppModel.filePath.size() : 0)+
                            (AudioModel.filePath!=null ? AudioModel.filePath.size() : 0)+
                            FileModel.getCount())+")");

            });
        }
        void bind(int position) {
            // use the sparse boolean array to check
            /* if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }*/

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                //checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                //checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }
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